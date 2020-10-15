package no.nav.personbruker.dittnav.tidslinje.api.beskjed

import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.config.getWithParameter
import java.net.URL

class BeskjedConsumer(
        private val client: HttpClient,
        private val eventHandlerBaseURL: URL,
        private val pathToEndpoint: URL = URL("$eventHandlerBaseURL/fetch/beskjed/grouped")
) {

    suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<Beskjed> {
        val completePathToEndpoint = URL("$pathToEndpoint")
        val externalEvents = getExternalEvents(innloggetBruker, completePathToEndpoint, grupperingsId, produsent)
        return externalEvents
    }

    private suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, completePathToEndpoint: URL, grupperingsId: String, produsent: String): List<Beskjed> {
        return client.getWithParameter(completePathToEndpoint, innloggetBruker, grupperingsId, produsent)
    }
}