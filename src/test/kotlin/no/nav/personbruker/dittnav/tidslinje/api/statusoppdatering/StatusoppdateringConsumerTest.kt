package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

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
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import java.net.URL

class StatusoppdateringConsumerTest {

    val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val grupperingsid = "Dok123"
    val produsent = "dittnav"

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
            install(JsonFeature) {
                serializer = buildJsonSerializer()
            }
        }
        val StatusoppdateringConsumer = StatusoppdateringConsumer(client, URL("http://event-handler"))

        runBlocking {
            StatusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) `should be equal to` emptyList()
        }
    }

    @Test
    fun `should get list of Statusoppdatering`() {
        val StatusoppdateringObject = createStatusoppdatering(eventId = "1", fodselsnummer = "1")
        val objectMapper = ObjectMapper().apply {
            enableDittNavJsonConfig()
        }
        val client = getClient {
            respond(
                    objectMapper.writeValueAsString(listOf(StatusoppdateringObject)),
                    headers = headersOf(HttpHeaders.ContentType,
                            ContentType.Application.Json.toString())
            )
        }
        val StatusoppdateringConsumer = StatusoppdateringConsumer(client, URL("http://event-handler"))

        runBlocking {
            val externalEvents = StatusoppdateringConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent)
            val event = externalEvents.first()
            externalEvents.size `should be equal to` 1
            event.statusGlobal `should be equal to` StatusoppdateringObject.statusGlobal
            event.statusIntern!! `should be equal to` StatusoppdateringObject.statusIntern!!
            event.sakstema `should be equal to` StatusoppdateringObject.sakstema
            event.fodselsnummer `should be equal to` StatusoppdateringObject.fodselsnummer
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
