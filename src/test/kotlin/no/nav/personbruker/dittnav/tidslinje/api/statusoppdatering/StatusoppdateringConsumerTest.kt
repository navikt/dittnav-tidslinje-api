package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

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
import org.junit.jupiter.api.Test
import java.net.URL

class StatusoppdateringConsumerTest {

    val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val grupperingsid = "Dok123"
    val systembruker = "dittnav"

    @Test
    fun `should call information endpoint on event handler`() {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    if (request.url.encodedPath.contains("/fetch/statusoppdatering/grouped") && request.url.host.contains("event-handler")) {
                        respond("[]", headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
                    } else {
                        respondError(HttpStatusCode.BadRequest)
                    }
                }
            }
            install(JsonFeature)
        }
        val statusoppdateringConsumer = StatusoppdateringConsumer(client, URL("http://event-handler"))

        runBlocking {
            statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker) `should be equal to` emptyList()
        }
    }

    @Test
    fun `should get list of Statusoppdatering`() {
        val statusoppdateringObject = createStatusoppdatering(eventId = "1", fodselsnummer = "1")

        val client = getClient {
            respond(
                    json().encodeToString(listOf(statusoppdateringObject)),
                    headers = headersOf(HttpHeaders.ContentType,
                            ContentType.Application.Json.toString())
            )
        }
        val statusoppdateringConsumer = StatusoppdateringConsumer(client, URL("http://event-handler"))

        runBlocking {
            val externalEvents = statusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, systembruker)
            val event = externalEvents.first()
            externalEvents.size `should be equal to` 1
            event.statusGlobal `should be equal to` statusoppdateringObject.statusGlobal
            event.statusIntern!! `should be equal to` statusoppdateringObject.statusIntern!!
            event.sakstema `should be equal to` statusoppdateringObject.sakstema
            event.fodselsnummer `should be equal to` statusoppdateringObject.fodselsnummer
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
