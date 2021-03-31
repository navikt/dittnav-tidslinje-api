package no.nav.personbruker.dittnav.tidslinje.api.config

import SYSTEMBRUKER_HEADER_NAME
import Systembruker
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import java.net.URL


suspend inline fun <reified T> HttpClient.get(url: URL, innloggetBruker: InnloggetBruker): T = withContext(Dispatchers.IO) {
    request {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, innloggetBruker.createAuthenticationHeader())
    }
}

suspend inline fun <reified T> HttpClient.getWithParameter(url: URL,
                                                           innloggetBruker: InnloggetBruker,
                                                           grupperingsId: String,
                                                           systembruker: Systembruker): T = withContext(Dispatchers.IO) {
    request {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, innloggetBruker.createAuthenticationHeader())
        header(SYSTEMBRUKER_HEADER_NAME, "Bearer $systembruker")
        parameter("grupperingsid", grupperingsId)
    }
}

