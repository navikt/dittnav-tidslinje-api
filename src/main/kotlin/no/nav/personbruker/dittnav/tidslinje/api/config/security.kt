import io.ktor.request.*
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.MissingHeaderException

typealias Systembruker = String

const val SYSTEMBRUKER_HEADER_NAME = "systembruker"

fun ApplicationRequest.systembrukerHeader(): Systembruker =
    this.header("systembruker")?.removePrefix("Bearer ") ?: throw MissingHeaderException("Header med systembruker mangler.")
