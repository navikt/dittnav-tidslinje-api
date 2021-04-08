package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.config.Systembruker
import no.nav.personbruker.dittnav.tidslinje.api.config.getWithParameter
import java.net.URL

class StatusoppdateringConsumer(
        private val client: HttpClient,
        private val eventHandlerBaseURL: URL,
        private val pathToEndpoint: URL = URL("$eventHandlerBaseURL/fetch/statusoppdatering/grouped")
) {

    suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, systembruker: Systembruker): List<Statusoppdatering> {
        return getExternalEvents(innloggetBruker, pathToEndpoint, grupperingsId, systembruker)
    }

    private suspend fun getExternalEvents(innloggetBruker: InnloggetBruker,
                                          completePathToEndpoint: URL,
                                          grupperingsId: String,
                                          systembruker: Systembruker): List<Statusoppdatering> {
        return client.getWithParameter(completePathToEndpoint, innloggetBruker, grupperingsId, systembruker)
    }
}
