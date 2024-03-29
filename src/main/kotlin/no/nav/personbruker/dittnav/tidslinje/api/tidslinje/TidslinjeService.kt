package no.nav.personbruker.dittnav.tidslinje.api.tidslinje

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedDTO
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedService
import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.BrukernotifikasjonDTO
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

    suspend fun getTidslinjeEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<BrukernotifikasjonDTO> {

        val allEvents = coroutineScope {
            val statusoppdateringEvents = async { statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsId, produsent) }
            val beskjedEvents = async { beskjedService.getBeskjedEvents(innloggetBruker, grupperingsId, produsent) }
            val oppgaveEvents = async { oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsId, produsent) }
            val innboksEvents = async { innboksService.getInnboksEvents(innloggetBruker, grupperingsId, produsent) }

            mergeLists(statusoppdateringEvents.await(), beskjedEvents.await(), oppgaveEvents.await(), innboksEvents.await())
        }
        return sortByEventTidspunkt(allEvents)
    }

    private fun mergeLists(statusoppdateringEvents: List<StatusoppdateringDTO>,
                           beskjedEvents: List<BeskjedDTO>,
                           oppgaveEvents: List<OppgaveDTO>,
                           innboksEvents: List<InnboksDTO>): List<BrukernotifikasjonDTO> {

        val allEvents = mutableListOf<BrukernotifikasjonDTO>()
        allEvents.addAll(statusoppdateringEvents)
        allEvents.addAll(beskjedEvents)
        allEvents.addAll(oppgaveEvents)
        allEvents.addAll(innboksEvents)
        return allEvents
    }

    private fun sortByEventTidspunkt(events: List<BrukernotifikasjonDTO>): List<BrukernotifikasjonDTO> {
        return events.sortedByDescending { event -> event.eventTidspunkt }
    }

}