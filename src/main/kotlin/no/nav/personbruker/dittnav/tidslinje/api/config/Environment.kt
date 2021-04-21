package no.nav.personbruker.dittnav.tidslinje.api.config

import java.net.URL

data class Environment(
        val eventHandlerURL: URL = URL("http://dummy.nav.no"),
        val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
        val corsAllowedSchemes: String = getOptionalEnvVar("CORS_ALLOWED_SCHEMES", "https"),
        val issoJwksUrl: URL = URL(getEnvVar("ISSO_JWKS_URL")),
        val issoIssuer: String = getEnvVar("ISSO_ISSUER"),
        val issoAcceptedAudience: List<String> = getEnvVar("ISSO_ACCEPTED_AUDIENCE")
            .split(",")
            .map(String::trim)
)

fun getEnvVar(varName: String): String {
    return System.getenv(varName)
            ?: throw IllegalArgumentException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}

fun getOptionalEnvVar(varName: String, defaultValue: String): String {
    return System.getenv(varName) ?: return defaultValue;
}
