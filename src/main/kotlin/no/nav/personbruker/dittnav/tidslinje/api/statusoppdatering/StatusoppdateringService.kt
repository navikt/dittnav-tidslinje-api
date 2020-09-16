package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.UntransformableRecordException

class StatusoppdateringService(private val statusoppdateringConsumer: StatusoppdateringConsumer) {

    suspend fun getStatusoppdateringEvents(innloggetBruker: InnloggetBruker): List<StatusoppdateringDTO> {
        val externalEvents = statusoppdateringConsumer.getExternalEvents(innloggetBruker)
        return getStatusoppdateringEvents(innloggetBruker, externalEvents)
    }

    private fun getStatusoppdateringEvents(innloggetBruker: InnloggetBruker, externalEvents: List<Statusoppdatering>): List<StatusoppdateringDTO> {
        return try {
            externalEvents.map { statusoppdatering ->
                transformToDTO(statusoppdatering, innloggetBruker)
            }
        } catch (exception: Exception) {
            throw UntransformableRecordException("Klarte ikke transformere den eksterne typen Statusoppdatering", exception)
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
