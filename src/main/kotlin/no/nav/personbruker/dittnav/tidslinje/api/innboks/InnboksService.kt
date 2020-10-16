package no.nav.personbruker.dittnav.tidslinje.api.innboks

import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException

class InnboksService(private val innboksConsumer: InnboksConsumer) {

    suspend fun getInnboksEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<InnboksDTO> {
        return getInnboksEvents(innloggetBruker) {
            innboksConsumer.getExternalEvents(innloggetBruker, grupperingsId, produsent)
        }
    }

    private suspend fun getInnboksEvents(innloggetBruker: InnloggetBruker, getEvents: suspend () -> List<Innboks>): List<InnboksDTO> {
        return try {
            val externalEvents = getEvents()
            externalEvents.map { innboks -> transformToDTO(innboks, innloggetBruker) }
        } catch (exception: Exception) {
            throw ConsumeEventException("Klarte ikke hente eventer av type Innboks", exception)
        }
    }

    private fun transformToDTO(innboks: Innboks, innloggetBruker: InnloggetBruker): InnboksDTO {
        return if (innloggetBrukerIsAllowedToViewAllDataInEvent(innboks, innloggetBruker)) {
            toInnboksDTO(innboks)
        } else {
            toMaskedInnboksDTO(innboks)
        }
    }

    private fun innloggetBrukerIsAllowedToViewAllDataInEvent(innboks: Innboks, innloggetBruker: InnloggetBruker): Boolean {
        return innloggetBruker.innloggingsnivaa >= innboks.sikkerhetsnivaa
    }
}
