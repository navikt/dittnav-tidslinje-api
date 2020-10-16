package no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon

import java.time.ZonedDateTime

interface BrukernotifikasjonDTO {
    val eventTidspunkt: ZonedDateTime
}