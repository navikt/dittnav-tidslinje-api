package no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon

import java.time.ZonedDateTime

interface Brukernotifikasjon {
    val eventTidspunkt: ZonedDateTime
}