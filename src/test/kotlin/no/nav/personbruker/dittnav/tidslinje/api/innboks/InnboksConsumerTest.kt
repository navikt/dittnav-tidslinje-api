package no.nav.personbruker.dittnav.tidslinje.api.innboks

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.config.json
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Test
import java.net.URL

class InnboksConsumerTest {

    val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val grupperingsid = "Dok123"
    val systembruker = "dittnav"

    @Test
    fun `should call innboks endpoint on event handler`() {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    if (request.url.encodedPath.contains("/fetch/innboks/grouped") && request.url.host.contains("event-handler")) {
                        respond("[]", headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
                    } else {
                        respondError(HttpStatusCode.BadRequest)
                    }
                }
            }
            install(JsonFeature)
        }
        val innboksConsumer = InnboksConsumer(client, URL("http://event-handler"))

        runBlocking {
            innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) `should be equal to` emptyList()
        }

    }

    @Test
    fun `should get list of Innboks`() {
        val innboksObject1 = createInnboks("1", "1", true)
        val innboksObject2 = createInnboks("2", "2", true)

        val client = getClient {
            respond(
                json().encodeToString(listOf(innboksObject1, innboksObject2)),
                headers = headersOf(HttpHeaders.ContentType,
                    ContentType.Application.Json.toString())
            )
        }
        val innboksConsumer = InnboksConsumer(client, URL("http://event-handler"))

        runBlocking {
            val externalEvents = innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker)
            val event = externalEvents.first()
            externalEvents.size `should be equal to` 2
            event.tekst `should be equal to` innboksObject1.tekst
            event.fodselsnummer `should be equal to` innboksObject1.fodselsnummer
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
