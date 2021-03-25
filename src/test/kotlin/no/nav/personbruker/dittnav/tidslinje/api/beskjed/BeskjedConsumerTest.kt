package no.nav.personbruker.dittnav.tidslinje.api.beskjed

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

class BeskjedConsumerTest {

    val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val grupperingsid = "Dok123"
    val produsent = "dittnav"

    @Test
    fun `should call information endpoint on event handler`() {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    if (request.url.encodedPath.contains("/fetch/beskjed/grouped") && request.url.host.contains("event-handler")) {
                        respond("[]", headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
                    } else {
                        respondError(HttpStatusCode.BadRequest)
                    }
                }
            }
            install(JsonFeature)
        }
        val beskjedConsumer = BeskjedConsumer(client, URL("http://event-handler"))

        runBlocking {
            beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) `should be equal to` emptyList()
        }
    }

    @Test
    fun `should get list of Beskjed`() {
        val beskjedObject = createBeskjed(eventId = "1", fodselsnummer = "1", uid = "1", aktiv = true)

        val client = getClient {
            respond(
                    json().encodeToString(listOf(beskjedObject)),
                    headers = headersOf(HttpHeaders.ContentType,
                            ContentType.Application.Json.toString())
            )
        }
        val beskjedConsumer = BeskjedConsumer(client, URL("http://event-handler"))

        runBlocking {
            val externalEvents = beskjedConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent)
            val event = externalEvents.first()
            externalEvents.size `should be equal to` 1
            event.tekst `should be equal to` beskjedObject.tekst
            event.fodselsnummer `should be equal to` beskjedObject.fodselsnummer
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
