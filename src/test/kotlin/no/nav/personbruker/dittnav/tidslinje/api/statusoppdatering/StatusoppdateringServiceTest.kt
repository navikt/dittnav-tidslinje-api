package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

class StatusoppdateringServiceTest {

    var innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val statusoppdateringConsumer = mockk<StatusoppdateringConsumer>()
    val statusoppdateringService = StatusoppdateringService(statusoppdateringConsumer)
    val grupperingsid = "Dok123"
    val produsent = "dittnav"

    @Test
    fun `should return list of statusoppdateringDTO when Events are received`() {
        val statusoppdatering1 = createStatusoppdatering("1", "1")
        val statusoppdatering2 = createStatusoppdatering("2", "2")

        coEvery {
            statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent)
        } returns listOf(statusoppdatering1, statusoppdatering2)

        runBlocking {
            val statusoppdateringList = statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsid, produsent)
            statusoppdateringList.size `should be equal to` 2
        }
    }

    @Test
    fun `should mask events with security level higher than current user`() {
        val ident = "1"
        var statusoppdatering = createStatusoppdatering("1", "1")
        statusoppdatering = statusoppdatering.copy(sikkerhetsnivaa = 4)
        innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(ident, 3)
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(statusoppdatering)
        runBlocking {
            val statusoppdateringList = statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsid, produsent)
            val statusoppdateringDTO = statusoppdateringList.first()
            statusoppdateringDTO.fodselsnummer `should be equal to` statusoppdatering.fodselsnummer
            statusoppdateringDTO.statusGlobal `should be equal to` "***"
            statusoppdateringDTO.statusIntern!! `should be equal to` "***"
            statusoppdateringDTO.sakstema `should be equal to` "***"
            statusoppdateringDTO.fodselsnummer `should be equal to` statusoppdatering.fodselsnummer
            statusoppdateringDTO.link `should be equal to` "***"
            statusoppdateringDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should not mask events with security level lower than current user`() {
        var statusoppdatering = createStatusoppdatering("1", "1")
        statusoppdatering = statusoppdatering.copy(sikkerhetsnivaa = 3)
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(statusoppdatering)
        runBlocking {
            val statusoppdateringList = statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsid, produsent)
            val statusoppdateringDTO = statusoppdateringList.first()
            statusoppdateringDTO.fodselsnummer `should be equal to` statusoppdatering.fodselsnummer
            statusoppdateringDTO.statusGlobal `should be equal to` statusoppdatering.statusGlobal
            statusoppdateringDTO.statusIntern!! `should be equal to` statusoppdatering.statusIntern!!
            statusoppdateringDTO.sakstema `should be equal to` statusoppdatering.sakstema
            statusoppdateringDTO.fodselsnummer `should be equal to` statusoppdatering.fodselsnummer
            statusoppdateringDTO.link `should be equal to` statusoppdatering.link
            statusoppdateringDTO.sikkerhetsnivaa `should be equal to` 3
        }
    }

    @Test
    fun `should not mask events with security level equal than current user`() {
        val statusoppdatering = createStatusoppdatering("1", "1")
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(statusoppdatering)
        runBlocking {
            val statusoppdateringList = statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsid, produsent)
            val statusoppdateringDTO = statusoppdateringList.first()
            statusoppdateringDTO.fodselsnummer `should be equal to` statusoppdatering.fodselsnummer
            statusoppdateringDTO.statusGlobal `should be equal to` statusoppdatering.statusGlobal
            statusoppdateringDTO.statusIntern!! `should be equal to` statusoppdatering.statusIntern!!
            statusoppdateringDTO.sakstema `should be equal to` statusoppdatering.sakstema
            statusoppdateringDTO.fodselsnummer `should be equal to` statusoppdatering.fodselsnummer
            statusoppdateringDTO.link `should be equal to` statusoppdatering.link
            statusoppdateringDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should throw exception if fetching events fails`() {
        coEvery {
            statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent)
        } throws Exception("Test error")

        invoking {
            runBlocking {
                statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsid, produsent)
            }
        } `should throw` ConsumeEventException::class
    }
}
