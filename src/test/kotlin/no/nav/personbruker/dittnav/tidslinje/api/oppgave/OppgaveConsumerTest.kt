package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.common.SystembrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.config.json
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Test
import java.net.URL

class OppgaveConsumerTest {

    val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val systembruker = SystembrukerObjectMother.createSystembruker()
    val grupperingsid = "Dok123"

    @Test
    fun `should call oppgave endpoint on event handler`() {

        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    if (request.url.encodedPath.contains("/fetch/oppgave/grouped") && request.url.host.contains("event-handler")) {
                        respond("[]", headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
                    } else {
                        respondError(HttpStatusCode.BadRequest)
                    }
                }
            }
            install(JsonFeature)
        }

        val oppgaveConsumer = OppgaveConsumer(client, URL("http://event-handler"))

        runBlocking {
            oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) `should be equal to` emptyList()
        }
    }

    @Test
    fun `should get list of Oppgave`() {
        val oppgaveObject1 = createOppgave(eventId = "1", fodselsnummer = "1", aktiv = true)
        val oppgaveObject2 = createOppgave(eventId = "2", fodselsnummer = "2", aktiv = true)

        val client = getClient {
            respond(
                    json().encodeToString(listOf(oppgaveObject1, oppgaveObject2)),
                    headers = headersOf(HttpHeaders.ContentType,
                            ContentType.Application.Json.toString())
            )
        }
        val oppgaveConsumer = OppgaveConsumer(client, URL("http://event-handler"))

        runBlocking {
            val externalEvents = oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker)
            val event = externalEvents.first()
            externalEvents.size `should be equal to` 2
            event.tekst `should be equal to` oppgaveObject1.tekst
            event.fodselsnummer `should be equal to` oppgaveObject1.fodselsnummer
            event.aktiv.`should be true`()
        }
    }

    private fun getClient(respond: MockRequestHandleScope.() -> HttpResponseData): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond()
                }
            }
            install(JsonFeature)
        }
    }
}
