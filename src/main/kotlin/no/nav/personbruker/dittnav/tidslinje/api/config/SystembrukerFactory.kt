package no.nav.personbruker.dittnav.tidslinje.api.config

import io.ktor.application.*
import io.ktor.auth.*

object SystembrukerFactory {

    fun createSystembruker(call: ApplicationCall): Systembruker {
        val principal = call.principal<SystembrukerPrincipal>()?: throw Exception("Finner ikke principal")
        return createSystembruker(principal)
    }

    private fun createSystembruker(principal: SystembrukerPrincipal): Systembruker {
        val token = principal.decodedJWT
        val ident: String = token.getClaim("pid").asString()
        return Systembruker(ident)
    }
}
