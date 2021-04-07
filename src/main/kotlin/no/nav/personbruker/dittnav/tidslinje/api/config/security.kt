import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.auth.jwt.*
import io.ktor.request.*
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.MissingHeaderException
import no.nav.personbruker.dittnav.tidslinje.api.config.Environment
import java.net.URL
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

typealias Systembruker = String

const val SYSTEMBRUKER_HEADER_NAME = "systembruker"

fun JWTAuthenticationProvider.Configuration.setupIssoAuthentication(environment: Environment) {
    val jwkProvider = initJwkProvider(environment.issoJwksUrl)
    val verifier = createVerifier(jwkProvider, environment.issoIssuer, environment.issoAcceptedAudience)
    val idToken = "systembrukerheader()"

    val decodedJWT = verifier(idToken)?.verify(idToken)
}

internal fun createVerifier(jwkProvider: JwkProvider, issuer: String, issoAcceptedAudience: List<String>): (String) -> JWTVerifier? = {
    jwkProvider.get(JWT.decode(it).keyId).tokenVerifier(
        issoAcceptedAudience,
        issuer
    )
}

internal fun Jwk.tokenVerifier(issoAcceptedAudience: List<String>, issuer: String): JWTVerifier =
    JWT.require(this.RSA256())
        .withAudience(*issoAcceptedAudience.toTypedArray())
        .withIssuer(issuer)
        .build()

fun initJwkProvider(securityJwksUri: URL): JwkProvider {
    return JwkProviderBuilder(securityJwksUri)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
}

private fun Jwk.RSA256() = Algorithm.RSA256(publicKey as RSAPublicKey, null)

fun ApplicationRequest.systembrukerHeader(): Systembruker =
    this.header("systembruker")?.removePrefix("Bearer ") ?: throw MissingHeaderException("Header med systembruker mangler.")
