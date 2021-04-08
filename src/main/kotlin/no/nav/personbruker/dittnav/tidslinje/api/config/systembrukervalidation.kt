package no.nav.personbruker.dittnav.tidslinje.api.config

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.MissingHeaderException
import java.net.URL
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

fun Authentication.Configuration.systembrukervalidation(environment: Environment) {

    val jwkProvider = initJwkProvider(environment.issoJwksUrl)
    val verifier = createVerifier(jwkProvider, environment.issoIssuer, environment.issoAcceptedAudience)
    val provider = IdTokenAuthenticationProvider.build("systembruker")

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val token = call.request.headers["systembruker"]?.removePrefix("Bearer ") ?: throw MissingHeaderException("Header med systembruker mangler.")
        val decoded = verifier(token)?.verify(token)
        if(decoded != null) {
            context.principal(SystembrukerPrincipal(decoded))
        } else {
            call.respond(HttpStatusCode.Forbidden, "Du må sende inn token!")
            finish()
        }
    }
}

private fun createVerifier(jwkProvider: JwkProvider, issuer: String, issoAcceptedAudience: List<String>): (String) -> JWTVerifier? = {
    jwkProvider.get(JWT.decode(it).keyId).tokenVerifier(
        issoAcceptedAudience,
        issuer
    )
}

private fun Jwk.tokenVerifier(issoAcceptedAudience: List<String>, issuer: String): JWTVerifier =
    JWT.require(this.RSA256())
        .withAudience(*issoAcceptedAudience.toTypedArray())
        .withIssuer(issuer)
        .build()

private fun initJwkProvider(securityJwksUri: URL): JwkProvider {
    return JwkProviderBuilder(securityJwksUri)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
}

private class IdTokenAuthenticationProvider constructor(config: Configuration) : AuthenticationProvider(config) {

    class Configuration(name: String?) : AuthenticationProvider.Configuration(name)

    companion object {
        fun build(name: String?) = IdTokenAuthenticationProvider(Configuration(name))
    }
}

private fun Jwk.RSA256() = Algorithm.RSA256(publicKey as RSAPublicKey, null)
