package no.nav.personbruker.dittnav.tidslinje.api.common.exception

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.slf4j.Logger
import java.lang.Exception

suspend fun respondWithError(call: ApplicationCall, log: Logger, exception: Exception) {
    when(exception) {
        is ConsumeEventException -> {
            call.respond(HttpStatusCode.ServiceUnavailable)
            val msg = "Klarte ikke hente eventer. Returnerer feilkode til frontend"
            log.error(msg, exception)
        }
        is FieldValidationException -> {
            call.respond(HttpStatusCode.BadRequest)
            val msg = "Klarte ikke hente eventer fordi vi fikk en valideringsfeil. Returnerer feilkode. {}"
            log.error(msg, exception)
        }
        else -> {
            call.respond(HttpStatusCode.InternalServerError)
            val msg = "Ukjent feil oppstod ved henting av eventer. Returnerer feilkode til frontend"
            log.error(msg, exception)
        }
    }
}
