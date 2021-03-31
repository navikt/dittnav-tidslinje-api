package no.nav.personbruker.dittnav.tidslinje.api.beskjed

import Systembruker
import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.config.getWithParameter
import java.net.URL

class BeskjedConsumer(
        private val client: HttpClient,
        private val eventHandlerBaseURL: URL,
        private val pathToEndpoint: URL = URL("$eventHandlerBaseURL/fetch/beskjed/grouped")
) {

    suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, systembruker: Systembruker): List<Beskjed> {
        return getExternalEvents(innloggetBruker, pathToEndpoint, grupperingsId, systembruker)
    }

    private suspend fun getExternalEvents(innloggetBruker: InnloggetBruker,
                                          completePathToEndpoint: URL,
                                          grupperingsId: String,
                                          systembruker: Systembruker): List<Beskjed> {
        return client.getWithParameter(completePathToEndpoint, innloggetBruker, grupperingsId, systembruker)
    }
}
