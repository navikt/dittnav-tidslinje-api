package no.nav.personbruker.dittnav.tidslinje.api.tidslinje

import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedDTO
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedService
import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.Brukernotifikasjon
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksDTO
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksService
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveDTO
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveService
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringDTO
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringService

class TidslinjeService(private val statusoppdateringService: StatusoppdateringService,
                       private val beskjedService: BeskjedService,
                       private val oppgaveService: OppgaveService,
                       private val innboksService: InnboksService
) {

    suspend fun getTidslinjeEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<Brukernotifikasjon> {
        val statusoppdateringEvents = statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsId, produsent)
        val beskjedEvents = beskjedService.getBeskjedEvents(innloggetBruker, grupperingsId, produsent)
        val oppgaveEvents = oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsId, produsent)
        val innboksEvents = innboksService.getInnboksEvents(innloggetBruker, grupperingsId, produsent)

        val allEvents = createOneListOfAllEvents(statusoppdateringEvents, beskjedEvents, oppgaveEvents, innboksEvents)

        return sortByEventTidspunkt(allEvents)
    }

    private fun createOneListOfAllEvents(statusoppdateringEvents: List<StatusoppdateringDTO>,
                                         beskjedEvents: List<BeskjedDTO>,
                                         oppgaveEvents: List<OppgaveDTO>,
                                         innboksEvents: List<InnboksDTO>): List<Brukernotifikasjon> {

        val allEvents = mutableListOf<Brukernotifikasjon>()
        allEvents.addAll(statusoppdateringEvents)
        allEvents.addAll(beskjedEvents)
        allEvents.addAll(oppgaveEvents)
        allEvents.addAll(innboksEvents)
        return allEvents
    }

    private fun sortByEventTidspunkt(events: List<Brukernotifikasjon>): List<Brukernotifikasjon> {
        return events.sortedByDescending { event -> event.eventTidspunkt }
    }

}