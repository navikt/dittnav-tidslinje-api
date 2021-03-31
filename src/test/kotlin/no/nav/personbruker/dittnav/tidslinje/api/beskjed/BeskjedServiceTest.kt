package no.nav.personbruker.dittnav.tidslinje.api.beskjed

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

class BeskjedServiceTest {

    var innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val beskjedConsumer = mockk<BeskjedConsumer>()
    val beskjedService = BeskjedService(beskjedConsumer)
    val grupperingsid = "Dok123"
    val systembruker = "dittnav"
    val fodselsnummer = "1"

    @Test
    fun `should return list of BeskjedDTO when Events are received`() {
        val beskjed1 = createBeskjed(eventId = "1", fodselsnummer = fodselsnummer, uid = "1", aktiv = true)
        val beskjed2 = createBeskjed(eventId = "2", fodselsnummer = "2", uid = "2", aktiv = true)
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(beskjed1, beskjed2)
        runBlocking {
            val beskjedList = beskjedService.getBeskjedEvents(innloggetBruker, grupperingsid, systembruker)
            beskjedList.size `should be equal to` 2
        }
    }

    @Test
    fun `should mask events with security level higher than current user`() {
        var beskjed = createBeskjed(eventId = "1", fodselsnummer = fodselsnummer, uid = "1", aktiv = true)
        beskjed = beskjed.copy(sikkerhetsnivaa = 4)
        innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(fodselsnummer, innloggingsnivaa = 3)
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(beskjed)
        runBlocking {
            val beskjedList = beskjedService.getBeskjedEvents(innloggetBruker, grupperingsid, systembruker)
            val beskjedDTO = beskjedList.first()
            beskjedDTO.tekst `should be equal to` "***"
            beskjedDTO.link `should be equal to` "***"
            beskjedDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should not mask events with security level lower than current user`() {
        var beskjed = createBeskjed(eventId = "1", fodselsnummer = fodselsnummer, uid = "1", aktiv = true)
        beskjed = beskjed.copy(sikkerhetsnivaa = 3)
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(beskjed)
        runBlocking {
            val beskjedList = beskjedService.getBeskjedEvents(innloggetBruker, grupperingsid, systembruker)
            val beskjedDTO = beskjedList.first()
            beskjedDTO.tekst `should be equal to` beskjed.tekst
            beskjedDTO.link `should be equal to` beskjed.link
            beskjedDTO.sikkerhetsnivaa `should be equal to` 3
        }
    }

    @Test
    fun `should not mask events with security level equal than current user`() {
        val beskjed = createBeskjed(eventId = "1", fodselsnummer = fodselsnummer, uid = "1", aktiv = true)
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(beskjed)
        runBlocking {
            val beskjedList = beskjedService.getBeskjedEvents(innloggetBruker, grupperingsid, systembruker)
            val beskjedDTO = beskjedList.first()
            beskjedDTO.tekst `should be equal to` beskjed.tekst
            beskjedDTO.link `should be equal to` beskjed.link
            beskjedDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should throw exception if fetching events fails`() {
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } throws Exception("error")
        invoking { runBlocking { beskjedService.getBeskjedEvents(innloggetBruker, grupperingsid, systembruker) } } `should throw` ConsumeEventException::class
    }

}
