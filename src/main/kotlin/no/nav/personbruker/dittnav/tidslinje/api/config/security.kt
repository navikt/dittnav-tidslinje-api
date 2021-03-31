import io.ktor.request.*

typealias Systembruker = String

const val SYSTEMBRUKER_HEADER_NAME = "systembruker"

fun ApplicationRequest.systembrukerHeader(): Systembruker =
    this.header("systembruker")?.removePrefix("Bearer ") ?: throw RuntimeException("Header med systembruker mangler.")
