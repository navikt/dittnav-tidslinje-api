package no.nav.personbruker.dittnav.tidslinje.api.tidslinje

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedConsumer
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedService
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.createBeskjed
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksConsumer
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksService
import no.nav.personbruker.dittnav.tidslinje.api.innboks.createInnboks
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveConsumer
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveService
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.createOppgave
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringConsumer
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringService
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.createStatusoppdatering
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class TidslinjeServiceTest {

    private var innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    private val beskjedConsumer = mockk<BeskjedConsumer>()
    private val oppgaveConsumer = mockk<OppgaveConsumer>()
    private val innboksConsumer = mockk<InnboksConsumer>()
    private val statusoppdateringConsumer = mockk<StatusoppdateringConsumer>()
    private val beskjedService = BeskjedService(beskjedConsumer)
    private val oppgaveService = OppgaveService(oppgaveConsumer)
    private val innboksService = InnboksService(innboksConsumer)
    private val statusoppdateringService = StatusoppdateringService(statusoppdateringConsumer)
    private val tidslinjeService = TidslinjeService(statusoppdateringService, beskjedService, oppgaveService, innboksService)
    private val grupperingsid = "Dok123"
    private val produsent = "dittnav"

    @Test
    fun `should throw exception if fetching events fail`() {
        coEvery {
            beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent)
        } throws Exception("error")
        invoking {
            runBlocking {
                tidslinjeService.getTidslinjeEvents(innloggetBruker, grupperingsid, produsent)
            }
        } `should throw` ConsumeEventException::class
    }

    @Test
    fun `should return list of all events when Events are received`() {
        val oppgave = createOppgave(eventId = "1", fodselsnummer = innloggetBruker.ident, aktiv = true)
        val beskjed = createBeskjed(eventId = "2", fodselsnummer = innloggetBruker.ident, uid = "123", aktiv = true)
        val innboks = createInnboks(eventId = "3", fodselsnummer = innloggetBruker.ident, aktiv = true)
        val statusoppdatering = createStatusoppdatering(eventId = "4", fodselsnummer = innloggetBruker.ident)

        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(oppgave)
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(beskjed)
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(innboks)
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(statusoppdatering)

        runBlocking {
            val tidslinjeEvents = tidslinjeService.getTidslinjeEvents(innloggetBruker, grupperingsid, produsent)
            tidslinjeEvents.size `should be equal to` 4
            tidslinjeEvents.first().size `should be equal to` 1
            tidslinjeEvents.first()[0].toString() `should contain` "BeskjedDTO"
        }
    }

    @Test
    fun `should return empty list if no events match ids`() {
        val noMatchGrupperingsid = "dummyId"

        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, produsent) } returns emptyList()
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, produsent) } returns emptyList()
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, produsent) } returns emptyList()
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, produsent) } returns emptyList()

        runBlocking {
            val tidslinjeEvents = tidslinjeService.getTidslinjeEvents(innloggetBruker, noMatchGrupperingsid, produsent)
            tidslinjeEvents `should equal` listOf(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }
}