package no.nav.personbruker.dittnav.tidslinje.api.config

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.application.*
import io.ktor.auth.jwt.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.util.pipeline.*
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.MissingHeaderException
import org.slf4j.LoggerFactory
import java.net.URL
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

const val systemuserHeaderName = "systemuser"

object SystemuserValidation {

    private lateinit var jwkurl: URL
    private lateinit var jwkProvider: JwkProvider
    private lateinit var issuer: String
    private lateinit var audience: List<String>

    private var log = LoggerFactory.getLogger(SystemuserValidation::class.java)

    fun initSystemuserValidation(environment: Environment) {
        jwkurl = environment.issoJwksUrl
        jwkProvider = initJwkProvider(environment.issoJwksUrl)
        issuer = environment.issoIssuer
        audience = environment.issoAcceptedAudience
    }

    suspend fun PipelineContext<Unit, ApplicationCall>.validateSystemuser(block: suspend () -> Unit) {
        val token = systembrukerHeader(call.request.headers)
        val verifier = createVerifier(jwkProvider, issuer, audience)
        val decodedJWT = verifier(token)?.verify(token)
    }

    private fun initJwkProvider(securityJwksUri: URL): JwkProvider {
        return JwkProviderBuilder(securityJwksUri)
            .build()
    }

    private fun createVerifier(
        jwkProvider: JwkProvider,
        issuer: String,
        issoAcceptedAudience: List<String>
    ): (String) -> JWTVerifier? = {
        jwkProvider.get(JWT.decode(it).keyId).tokenVerifier(issoAcceptedAudience, issuer)
    }

    private fun Jwk.tokenVerifier(issoAcceptedAudience: List<String>, issuer: String): JWTVerifier {
        return JWT.require(this.RSA256())
            .withAudience(*issoAcceptedAudience.toTypedArray())
            .withIssuer(issuer)
            .build()
    }

    private fun Jwk.RSA256() = Algorithm.RSA256(publicKey as RSAPublicKey, null)

    private fun systembrukerHeader(headers: Headers) =
        headers[systemuserHeaderName]?.removePrefix("Bearer ")
            ?: throw MissingHeaderException("Header med systembruker mangler.")
}

