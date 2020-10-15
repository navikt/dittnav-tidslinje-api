package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.config.getWithParameter
import java.net.URL

class OppgaveConsumer(
        private val client: HttpClient,
        private val eventHandlerBaseURL: URL,
        private val pathToEndpoint: URL = URL("$eventHandlerBaseURL/fetch/oppgave/grouped")
) {

    suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, produsent: String): List<Oppgave> {
        val completePathToEndpoint = URL("$pathToEndpoint")
        val externalActiveEvents = getExternalEvents(innloggetBruker, completePathToEndpoint, grupperingsId, produsent)
        return externalActiveEvents
    }

    private suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, comletePathToEndpoint: URL, grupperingsId: String, produsent: String): List<Oppgave> {
        return client.getWithParameter(comletePathToEndpoint, innloggetBruker, grupperingsId, produsent)
    }
}
