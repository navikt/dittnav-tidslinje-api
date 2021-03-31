package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import Systembruker
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException

class OppgaveService(private val oppgaveConsumer: OppgaveConsumer) {

    suspend fun getOppgaveEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, systembruker: Systembruker): List<OppgaveDTO> {
        return getOppgaveEvents(innloggetBruker) {
            oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsId, systembruker)
        }
    }

    private suspend fun getOppgaveEvents(innloggetBruker: InnloggetBruker, getEvents: suspend () -> List<Oppgave>): List<OppgaveDTO> {
        return try {
            val externalEvents = getEvents()
            externalEvents.map { oppgave -> transformToDTO(oppgave, innloggetBruker) }
        } catch (exception: Exception) {
            throw ConsumeEventException("Klarte ikke hente eventer av type Oppgave", exception)
        }
    }

    private fun transformToDTO(oppgave: Oppgave, innloggetBruker: InnloggetBruker): OppgaveDTO {
        return if (innloggetBrukerIsAllowedToViewAllDataInEvent(oppgave, innloggetBruker)) {
            toOppgaveDTO(oppgave)
        } else {
            toMaskedOppgaveDTO(oppgave)
        }
    }

    private fun innloggetBrukerIsAllowedToViewAllDataInEvent(oppgave: Oppgave, innloggetBruker: InnloggetBruker): Boolean {
        return innloggetBruker.innloggingsnivaa >= oppgave.sikkerhetsnivaa
    }
}
