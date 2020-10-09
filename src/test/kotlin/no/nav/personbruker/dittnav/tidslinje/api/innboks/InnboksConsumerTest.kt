package no.nav.personbruker.dittnav.tidslinje.api.innboks

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.tidslinje.api.config.buildJsonSerializer
import no.nav.personbruker.dittnav.tidslinje.api.config.enableDittNavJsonConfig
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import java.net.URL

class InnboksConsumerTest {

    val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val grupperingsid = "Dok123"
    val produsent = "dittnav"

    @Test
    fun `should call innboks endpoint on event handler`() {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    if (request.url.encodedPath.contains("/fetch/grouped/innboks") && request.url.host.contains("event-handler")) {
                        respond("[]", headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
                    } else {
                        respondError(HttpStatusCode.BadRequest)
                    }
                }
            }
            install(JsonFeature) {
                serializer = buildJsonSerializer()
            }
        }
        val innboksConsumer = InnboksConsumer(client, URL("http://event-handler"))

        runBlocking {
            innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) `should equal` emptyList()
        }

    }

    @Test
    fun `should get list of Innboks`() {
        val innboksObject1 = createInnboks("1", "1", true)
        val innboksObject2 = createInnboks("2", "2", true)

        val objectMapper = ObjectMapper().apply {
            enableDittNavJsonConfig()
        }

        val client = getClient {
            respond(
                    objectMapper.writeValueAsString(listOf(innboksObject1, innboksObject2)),
                    headers = headersOf(HttpHeaders.ContentType,
                            ContentType.Application.Json.toString())
            )
        }
        val innboksConsumer = InnboksConsumer(client, URL("http://event-handler"))

        runBlocking {
            val externalEvents = innboksConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent)
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
            install(JsonFeature) {
                serializer = buildJsonSerializer()
            }
        }
    }
}
