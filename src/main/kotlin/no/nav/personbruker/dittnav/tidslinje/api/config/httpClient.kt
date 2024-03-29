package no.nav.personbruker.dittnav.tidslinje.api.config

import io.ktor.client.HttpClient
import io.ktor.client.features.timeout
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import java.net.URL


suspend inline fun <reified T> HttpClient.get(url: URL, innloggetBruker: InnloggetBruker): T = withContext(Dispatchers.IO) {
    request<T> {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, innloggetBruker.createAuthenticationHeader())
    }
}

suspend inline fun <reified T> HttpClient.getWithParameter(url: URL,
                                                           innloggetBruker: InnloggetBruker,
                                                           grupperingsId: String,
                                                           produsent: String): T = withContext(Dispatchers.IO) {
    request<T> {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, innloggetBruker.createAuthenticationHeader())
        parameter("grupperingsid", grupperingsId)
        parameter("produsent", produsent)
    }
}

suspend inline fun <reified T> HttpClient.getExtendedTimeout(url: URL, innloggetBruker: InnloggetBruker): T = withContext(Dispatchers.IO) {
    request<T> {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, innloggetBruker.createAuthenticationHeader())
        timeout {
            socketTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            requestTimeoutMillis = 40000
        }
    }
}

