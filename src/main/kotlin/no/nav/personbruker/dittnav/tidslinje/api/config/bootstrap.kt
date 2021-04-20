package no.nav.personbruker.dittnav.tidslinje.api.config

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.client.*
import io.ktor.client.features.json.serializer.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedConsumer
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedService
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBruker
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerFactory
import no.nav.personbruker.dittnav.tidslinje.api.health.authenticationCheck
import no.nav.personbruker.dittnav.tidslinje.api.health.healthApi
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksConsumer
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksService
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveConsumer
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveService
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringConsumer
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringService
import no.nav.personbruker.dittnav.tidslinje.api.tidslinje.TidslinjeService
import no.nav.personbruker.dittnav.tidslinje.api.tidslinje.tidslinje
import no.nav.security.token.support.ktor.tokenValidationSupport
import setupIssoAuthentication

@KtorExperimentalAPI
fun Application.mainModule() {
    val environment = Environment()

    DefaultExports.initialize()
    SystemuserValidation.initSystemuserValidation(environment)

    val httpClient = HttpClientBuilder.build(KotlinxSerializer(json()))

    val beskjedConsumer = BeskjedConsumer(httpClient, environment.eventHandlerURL)
    val oppgaveConsumer = OppgaveConsumer(httpClient, environment.eventHandlerURL)
    val statusoppdateringConsumer = StatusoppdateringConsumer(httpClient, environment.eventHandlerURL)
    val innboksConsumer = InnboksConsumer(httpClient, environment.eventHandlerURL)

    val beskjedService = BeskjedService(beskjedConsumer)
    val oppgaveService = OppgaveService(oppgaveConsumer)
    val statusoppdateringService = StatusoppdateringService(statusoppdateringConsumer)
    val innboksService = InnboksService(innboksConsumer)
    val tidslinjeService = TidslinjeService(statusoppdateringService, beskjedService, oppgaveService, innboksService)


    install(DefaultHeaders)

    install(CORS) {
        host(environment.corsAllowedOrigins, schemes = listOf(environment.corsAllowedSchemes))
        allowCredentials = true
        header(HttpHeaders.ContentType)
    }

    val config = this.environment.config

    install(Authentication) {
        tokenValidationSupport(config = config)
    }

    install(ContentNegotiation) {
        json(no.nav.personbruker.dittnav.tidslinje.api.config.json())
    }

    routing {
        healthApi(httpClient, environment)
        authenticate {
            tidslinje(tidslinjeService)
            authenticationCheck()
        }
        configureShutdownHook(httpClient)
    }
}

private fun Application.configureShutdownHook(httpClient: HttpClient) {
    environment.monitor.subscribe(ApplicationStopping) {
        httpClient.close()
    }
}

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: InnloggetBruker
    get() = InnloggetBrukerFactory.createNewInnloggetBruker(call.authentication.principal())
