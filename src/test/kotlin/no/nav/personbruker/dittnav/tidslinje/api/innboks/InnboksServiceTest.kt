package no.nav.personbruker.dittnav.tidslinje.api.innboks

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

class InnboksServiceTest {
    val innboksConsumer = mockk<InnboksConsumer>()
    val innboksService = InnboksService(innboksConsumer)
    var innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val grupperingsid = "Dok123"
    val produsent = "dittnav"
    val fodselsnummer = "1"

    @Test
    fun `should return list of InnboksDTO when Events are received`() {
        val innboks1 = createInnboks(eventId = "1", fodselsnummer = "1", aktiv = true)
        val innboks2 = createInnboks(eventId = "2", fodselsnummer = "2", aktiv = true)
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(innboks1, innboks2)
        runBlocking {
            val innboksList = innboksService.getInnboksEvents(innloggetBruker, grupperingsid, produsent)
            innboksList.size `should be equal to` 2
        }
    }

    @Test
    fun `should mask events with security level higher than current user`() {
        var innboks = createInnboks("1", fodselsnummer, true)
        innboks = innboks.copy(sikkerhetsnivaa = 4)
        innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(fodselsnummer, 3)
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(innboks)
        runBlocking {
            val innboksList = innboksService.getInnboksEvents(innloggetBruker, grupperingsid, produsent)
            val innboksDTO = innboksList.first()
            innboksDTO.tekst `should be equal to` "***"
            innboksDTO.link `should be equal to` "***"
            innboksDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should not mask events with security level lower than current user`() {
        var innboks = createInnboks("1", "1", true)
        innboks = innboks.copy(sikkerhetsnivaa = 3)
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(innboks)
        runBlocking {
            val innboksList = innboksService.getInnboksEvents(innloggetBruker, grupperingsid, produsent)
            val innboksDTO = innboksList.first()
            innboksDTO.tekst `should be equal to` innboks.tekst
            innboksDTO.link `should be equal to` innboks.link
            innboksDTO.sikkerhetsnivaa `should be equal to` 3
        }
    }

    @Test
    fun `should not mask events with security level equal than current user`() {
        val innboks = createInnboks("1", "1", true)
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(innboks)
        runBlocking {
            val innboksList = innboksService.getInnboksEvents(innloggetBruker, grupperingsid, produsent)
            val innboksDTO = innboksList.first()
            innboksDTO.tekst `should be equal to` innboks.tekst
            innboksDTO.link `should be equal to` innboks.link
            innboksDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should throw exception if fetching events fail`() {
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } throws Exception("error")
        invoking {
            runBlocking {
                innboksService.getInnboksEvents(innloggetBruker, grupperingsid, produsent)
            }
        } `should throw` ConsumeEventException::class
    }

}
