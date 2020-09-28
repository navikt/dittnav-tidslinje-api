package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.respondWithError
import no.nav.personbruker.dittnav.tidslinje.api.config.innloggetBruker
import org.slf4j.LoggerFactory

fun Route.statusoppdatering(statusoppdatreingService: StatusoppdateringService) {

    val log = LoggerFactory.getLogger(StatusoppdateringService::class.java)

    get("/statusoppdatering") {
        try {
            val grupperingsId = checkNotNull(call.request.queryParameters["grupperingsId"])
            val produsent = checkNotNull(call.request.queryParameters["produsent"])
            val statusoppdateringEvents = statusoppdatreingService.getStatusoppdateringEvents(innloggetBruker, grupperingsId, produsent)
            call.respond(HttpStatusCode.OK, statusoppdateringEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}

private fun checkNotNull(parameterFromRequest: String?): String {
    var result = ""
    parameterFromRequest?.let { parameter -> result = parameter }
    return result
}

