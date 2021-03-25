package no.nav.personbruker.dittnav.tidslinje.api.health

import io.ktor.application.ApplicationCall
import io.ktor.client.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.nav.personbruker.dittnav.tidslinje.api.config.Environment
import no.nav.personbruker.dittnav.tidslinje.api.config.HttpClientBuilder
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import kotlinx.html.*
import java.net.URL

suspend fun ApplicationCall.pingDependencies(client: HttpClient, environment: Environment) = coroutineScope {

    val eventHandlerPingableURL = URL("${environment.eventHandlerURL}/internal/isAlive")
    val eventHandlerSelftestStatus = async { getStatus(eventHandlerPingableURL, client) }
    val services = mapOf("DITTNAV_EVENT_HANDLER:" to eventHandlerSelftestStatus.await())

    val serviceStatus = if (services.values.any { it.status == Status.ERROR }) Status.ERROR else Status.OK

    respondHtml(status =
    if(Status.ERROR == serviceStatus) {
        HttpStatusCode.ServiceUnavailable
    } else {
        HttpStatusCode.OK
    })
    {
        head {
            title { +"Selftest dittnav-tidslinje-api" }
        }
        body {
            h1 {
                style = if (serviceStatus == Status.OK) "background: green" else "background: red;font-weight:bold"
                +"Service status: $serviceStatus"
            }
            table {
                thead {
                    tr { th { +"SELFTEST DITTNAV-TIDSLINJE-API" } }
                }
                tbody {
                    services.map {
                        tr {
                            td { +it.key }
                            td { +it.value.pingedURL.toString() }
                            td {
                                style = if (it.value.status == Status.OK) "background: green" else "background: red;font-weight:bold"
                                +it.value.status.toString()
                            }
                            td { +it.value.statusMessage }
                        }
                    }
                }
            }
        }
    }
}

