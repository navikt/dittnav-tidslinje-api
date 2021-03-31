package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import Systembruker
import io.ktor.client.HttpClient
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.config.getWithParameter
import java.net.URL

class OppgaveConsumer(
        private val client: HttpClient,
        private val eventHandlerBaseURL: URL,
        private val pathToEndpoint: URL = URL("$eventHandlerBaseURL/fetch/oppgave/grouped")
) {

    suspend fun getExternalEvents(innloggetBruker: InnloggetBruker, grupperingsId: String, systembruker: Systembruker): List<Oppgave> {
        return getExternalEvents(innloggetBruker, pathToEndpoint, grupperingsId, systembruker)
    }

    private suspend fun getExternalEvents(innloggetBruker: InnloggetBruker,
                                          completePathToEndpoint: URL,
                                          grupperingsId: String,
                                          systembruker: Systembruker): List<Oppgave> {
        return client.getWithParameter(completePathToEndpoint, innloggetBruker, grupperingsId, systembruker)
    }
}
