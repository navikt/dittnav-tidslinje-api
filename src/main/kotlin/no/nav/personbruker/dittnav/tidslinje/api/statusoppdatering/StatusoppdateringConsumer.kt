package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import no.nav.personbruker.dittnav.tidslinje.api.config.get
import java.net.URL

class StatusoppdateringConsumer(
        private val client: HttpClient,
        private val eventHandlerBaseURL: URL,
        private val pathToEndpoint: URL = URL("$eventHandlerBaseURL/fetch/statusoppdatering")
) {

    suspend fun getExternalEvents(innloggetBruker: InnloggetBruker,
                                  grupperingsId: String,
                                  produsent: String): List<Statusoppdatering> {
        try {
            return getExternalEvents(innloggetBruker, pathToEndpoint, grupperingsId, produsent)
        } catch (exception: Exception) {
            throw ConsumeEventException("Klarte ikke hente eksterne eventer av typen Statusoppdatering", exception)
        }
    }

    private suspend fun getExternalEvents(innloggetBruker: InnloggetBruker,
                                          completePathToEndpoint: URL,
                                          grupperingsId: String,
                                          produsent: String): List<Statusoppdatering> {
        return client.get(completePathToEndpoint, innloggetBruker, grupperingsId, produsent)
    }
}
