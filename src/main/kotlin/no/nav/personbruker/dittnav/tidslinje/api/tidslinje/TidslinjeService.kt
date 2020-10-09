package no.nav.personbruker.dittnav.tidslinje.api.tidslinje

import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedService
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksService
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveService
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringService

class TidslinjeService(private val statusoppdateringService: StatusoppdateringService,
                       private val beskjedService: BeskjedService,
                       private val oppgaveService: OppgaveService,
                       private val innboksService: InnboksService
) {

    suspend fun getTidslinjeEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<List<Any>> {
        val beskjedEvents = beskjedService.getBeskjedEvents(innloggetBruker, grupperingsId, produsent)
        val oppgaveEvents = oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsId, produsent)
        val innboksEvents = innboksService.getInnboksEvents(innloggetBruker, grupperingsId, produsent)
        val statusoppdateringEvents = statusoppdateringService.getStatusoppdateringEvents(innloggetBruker, grupperingsId, produsent)

        return listOf(beskjedEvents, oppgaveEvents, innboksEvents, statusoppdateringEvents)
    }

}