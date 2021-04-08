package no.nav.personbruker.dittnav.tidslinje.api.config

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.auth.*

internal data class SystembrukerPrincipal(val decodedJWT: DecodedJWT) : Principal
