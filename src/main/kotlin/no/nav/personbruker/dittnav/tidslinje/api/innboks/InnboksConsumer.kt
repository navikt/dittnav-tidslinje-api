package no.nav.personbruker.dittnav.tidslinje.api.innboks

import Systembruker
import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.config.getWithParameter
import java.net.URL

class InnboksConsumer(
        private val client: HttpClient,
        private val eventHandlerBaseURL: URL,
        private val pathToEndpoint: URL = URL("$eventHandlerBaseURL/fetch/innboks/grouped")
) {

    suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, systembruker: Systembruker): List<Innboks> {
        return getExternalEvents(innloggetBruker, pathToEndpoint, grupperingsId, systembruker)
    }

    private suspend fun getExternalEvents(innloggetBruker: InnloggetBruker,
                                          completePathToEndpoint: URL,
                                          grupperingsId: String,
                                          systembruker: Systembruker): List<Innboks> {
        return client.getWithParameter(completePathToEndpoint, innloggetBruker, grupperingsId, systembruker)
    }
}
