package no.nav.personbruker.dittnav.tidslinje.api.tidslinje

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.*
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import no.nav.personbruker.dittnav.tidslinje.api.innboks.*
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.*
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.*
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

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
    private val systembruker = "dittnav"

    @Test
    fun `should throw exception if fetching events fail`() {
        coEvery {
            beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker)
        } throws Exception("error")
        invoking {
            runBlocking {
                tidslinjeService.getTidslinjeEvents(innloggetBruker, grupperingsid, systembruker)
            }
        } `should throw` ConsumeEventException::class
    }

    @Test
    fun `should return list of all events when Events are received`() {
        val oppgave = createOppgave(eventId = "1", fodselsnummer = innloggetBruker.ident, aktiv = true)
        val beskjed = createBeskjed(eventId = "2", fodselsnummer = innloggetBruker.ident, uid = "123", aktiv = true)
        val innboks = createInnboks(eventId = "3", fodselsnummer = innloggetBruker.ident, aktiv = true)
        val statusoppdatering = createStatusoppdatering(eventId = "4", fodselsnummer = innloggetBruker.ident)

        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(oppgave)
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(beskjed)
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(innboks)
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(statusoppdatering)

        runBlocking {
            val tidslinjeEvents = tidslinjeService.getTidslinjeEvents(innloggetBruker, grupperingsid, systembruker)
            tidslinjeEvents.size `should be equal to` 4
        }
    }

    @Test
    fun `should return empty list if no events match grupperingsid`() {
        val noMatchGrupperingsid = "dummyId"

        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, systembruker) } returns emptyList()
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, systembruker) } returns emptyList()
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, systembruker) } returns emptyList()
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, noMatchGrupperingsid, systembruker) } returns emptyList()

        runBlocking {
            val tidslinjeEvents = tidslinjeService.getTidslinjeEvents(innloggetBruker, noMatchGrupperingsid, systembruker)
            tidslinjeEvents.`should be empty`()
        }
    }

    @Test
    fun `should return list that is sorted by EventTidspunkt`() {
        val oneDayAgo = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).minusDays(1)
        val twoDaysAgo = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).minusDays(2)
        val threeDaysAgo = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).minusDays(3)
        val fourDaysAgo = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).minusDays(4)

        val innboksOneDayAgo = createInnboksWithEventTidspunkt(eventId = "3", eventTidspunkt = oneDayAgo)
        val beskjedTwoDaysAgo = createBeskjedWithEventTidspunkt(eventId = "2", uid = "123", eventTidspunkt = twoDaysAgo)
        val statusoppdateringThreeDaysAgo = createStatusoppdateringWithEventTidspunkt(eventId = "4", eventTidspunkt = threeDaysAgo)
        val oppgaveFourDaysAgo = createOppgaveWithEventTidspunkt(eventId = "1", eventTidspunkt = fourDaysAgo)

        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(oppgaveFourDaysAgo)
        coEvery { beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(beskjedTwoDaysAgo)
        coEvery { innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(innboksOneDayAgo)
        coEvery { statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) } returns listOf(statusoppdateringThreeDaysAgo)

        runBlocking {
            val tidslinjeEvents = tidslinjeService.getTidslinjeEvents(innloggetBruker, grupperingsid, systembruker)
            tidslinjeEvents.size `should be equal to` 4

            tidslinjeEvents[0].shouldBeInstanceOf<InnboksDTO>()
            tidslinjeEvents[0].eventTidspunkt `should be equal to` oneDayAgo

            tidslinjeEvents[1].shouldBeInstanceOf<BeskjedDTO>()
            tidslinjeEvents[1].eventTidspunkt `should be equal to` twoDaysAgo

            tidslinjeEvents[2].shouldBeInstanceOf<StatusoppdateringDTO>()
            tidslinjeEvents[2].eventTidspunkt `should be equal to` threeDaysAgo

            tidslinjeEvents[3].shouldBeInstanceOf<OppgaveDTO>()
            tidslinjeEvents[3].eventTidspunkt `should be equal to` fourDaysAgo
        }
    }

}
