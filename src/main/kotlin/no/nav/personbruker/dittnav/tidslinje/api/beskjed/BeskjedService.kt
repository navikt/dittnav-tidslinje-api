package no.nav.personbruker.dittnav.tidslinje.api.beskjed

import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException

class BeskjedService(private val beskjedConsumer: BeskjedConsumer) {

    suspend fun getBeskjedEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<BeskjedDTO> {
        return getBeskjedEvents(innloggetBruker) {
            beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsId, produsent)
        }
    }

    private suspend fun getBeskjedEvents(innloggetBruker: InnloggetBruker, getEvents: suspend () -> List<Beskjed>): List<BeskjedDTO> {
        return try {
            val externalEvents = getEvents()
            externalEvents.map { beskjed -> transformToDTO(beskjed, innloggetBruker) }
        } catch (exception: Exception) {
            throw ConsumeEventException("Klarte ikke hente eventer av type Beskjed", exception)
        }
    }

    private fun transformToDTO(beskjed: Beskjed, innloggetBruker: InnloggetBruker): BeskjedDTO {
        return if (innloggetBrukerIsAllowedToViewAllDataInEvent(beskjed, innloggetBruker)) {
            toBeskjedDTO(beskjed)
        } else {
            toMaskedBeskjedDTO(beskjed)
        }
    }

    private fun innloggetBrukerIsAllowedToViewAllDataInEvent(beskjed: Beskjed, innloggetBruker: InnloggetBruker): Boolean {
        return innloggetBruker.innloggingsnivaa >= beskjed.sikkerhetsnivaa
    }
}