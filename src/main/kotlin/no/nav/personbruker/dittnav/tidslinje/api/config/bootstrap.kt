package no.nav.personbruker.dittnav.tidslinje.api.config

import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.client.HttpClient
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpHeaders
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.util.pipeline.PipelineContext
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

fun Application.mainModule() {
    val environment = Environment()

    DefaultExports.initialize()

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
