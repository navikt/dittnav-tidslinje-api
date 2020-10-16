package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException

class StatusoppdateringService(private val statusoppdateringConsumer: StatusoppdateringConsumer) {

    suspend fun getStatusoppdateringEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<StatusoppdateringDTO> {
        return getStatusoppdateringEvents(innloggetBruker) {
            statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsId, produsent)
        }
    }

    private suspend fun getStatusoppdateringEvents(innloggetBruker: InnloggetBruker, getEvents: suspend () -> List<Statusoppdatering>): List<StatusoppdateringDTO> {
        return try {
            val externalEvents = getEvents()
            externalEvents.map { statusoppdatering -> transformToDTO(statusoppdatering, innloggetBruker) }
        } catch (exception: Exception) {
            throw ConsumeEventException("Klarte ikke hente eventer av type Statusoppdatering", exception)
        }
    }

    private fun transformToDTO(statusoppdatering: Statusoppdatering, innloggetBruker: InnloggetBruker): StatusoppdateringDTO {
        return if (innloggetBrukerIsAllowedToViewAllDataInEvent(statusoppdatering, innloggetBruker)) {
            toStatusoppdateringDTO(statusoppdatering)
        } else {
            toMaskedStatusoppdateringDTO(statusoppdatering)
        }
    }

    private fun innloggetBrukerIsAllowedToViewAllDataInEvent(statusoppdatering: Statusoppdatering, innloggetBruker: InnloggetBruker): Boolean {
        return innloggetBruker.innloggingsnivaa >= statusoppdatering.sikkerhetsnivaa
    }
}
