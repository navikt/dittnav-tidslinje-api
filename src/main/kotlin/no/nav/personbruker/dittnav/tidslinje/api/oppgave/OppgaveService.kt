package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker

class OppgaveService(private val oppgaveConsumer: OppgaveConsumer) {

    suspend fun getActiveOppgaveEvents(innloggetBruker: InnloggetBruker): List<OppgaveDTO> {
        return getOppgaveEvents(innloggetBruker) {
            oppgaveConsumer.getExternalActiveEvents(it)
        }
    }

    suspend fun getInactiveOppgaveEvents(innloggetBruker: InnloggetBruker): List<OppgaveDTO> {
        return getOppgaveEvents(innloggetBruker) {
            oppgaveConsumer.getExternalInactiveEvents(it)
        }
    }

    private suspend fun getOppgaveEvents(
            innloggetBruker: InnloggetBruker,
            getEvents: suspend (InnloggetBruker) -> List<Oppgave>
    ): List<OppgaveDTO> {
        return try {
            val externalEvents = getEvents(innloggetBruker)
            externalEvents.map { oppgave -> transformToDTO(oppgave, innloggetBruker) }
        } catch (exception: Exception) {
            throw ConsumeEventException("Klarte ikke hente eventer av type Oppgave", exception)
        }
    }

    private fun transformToDTO(oppgave: Oppgave, innloggetBruker: InnloggetBruker): OppgaveDTO {
        return if(innloggetBrukerIsAllowedToViewAllDataInEvent(oppgave, innloggetBruker)) {
            toOppgaveDTO(oppgave)
        } else {
            toMaskedOppgaveDTO(oppgave)
        }
    }

    private fun innloggetBrukerIsAllowedToViewAllDataInEvent(oppgave: Oppgave, innloggetBruker: InnloggetBruker): Boolean {
        return innloggetBruker.innloggingsnivaa >= oppgave.sikkerhetsnivaa
    }
}