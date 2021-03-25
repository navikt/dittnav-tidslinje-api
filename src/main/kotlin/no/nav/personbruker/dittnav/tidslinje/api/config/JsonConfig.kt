package no.nav.personbruker.dittnav.tidslinje.api.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import no.nav.personbruker.dittnav.tidslinje.api.beskjed.BeskjedDTO
import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.BrukernotifikasjonDTO
import no.nav.personbruker.dittnav.tidslinje.api.innboks.InnboksDTO
import no.nav.personbruker.dittnav.tidslinje.api.oppgave.OppgaveDTO
import no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering.StatusoppdateringDTO

fun json(): Json {
    return Json {
        this.serializersModule = SerializersModule {
            polymorphic(BrukernotifikasjonDTO::class) {
                subclass(BeskjedDTO::class)
                subclass(OppgaveDTO::class)
                subclass(InnboksDTO::class)
                subclass(StatusoppdateringDTO::class)
            }
        }
    }
}
