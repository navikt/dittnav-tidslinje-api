package no.nav.personbruker.dittnav.tidslinje.api.tidslinje

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.respondWithError
import no.nav.personbruker.dittnav.tidslinje.api.common.validation.validateNonNullFieldMaxLength
import no.nav.personbruker.dittnav.tidslinje.api.config.innloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.config.systembruker
import org.slf4j.LoggerFactory

fun Route.tidslinje(tidslinjeService: TidslinjeService) {

    val log = LoggerFactory.getLogger(TidslinjeService::class.java)

    get("/tidslinje") {
        try {
            val grupperingsId = validateNonNullFieldMaxLength(call.request.queryParameters["grupperingsid"], "grupperingsid", 100)
            val systembruker = systembruker
            val tidslinjeEvents = tidslinjeService.getTidslinjeEvents(innloggetBruker, grupperingsId, systembruker)
            call.respond(HttpStatusCode.OK, tidslinjeEvents)
        } catch (exception: Exception) {
            respondWithError(call, log, exception)
        }
    }
}
