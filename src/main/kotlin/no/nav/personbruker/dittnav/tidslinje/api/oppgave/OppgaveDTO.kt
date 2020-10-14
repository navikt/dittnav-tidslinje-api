package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.Brukernotifikasjon
import java.time.ZonedDateTime

data class OppgaveDTO(
        @JsonInclude(JsonInclude.Include.NON_NULL) val uid: String?,
        override val eventTidspunkt: ZonedDateTime,
        val eventId: String,
        val tekst: String,
        val link: String,
        val produsent: String?,
        val sistOppdatert: ZonedDateTime,
        val sikkerhetsnivaa: Int
) : Brukernotifikasjon {
    constructor(
            eventTidspunkt: ZonedDateTime,
            eventId: String,
            tekst: String,
            link: String,
            produsent: String?,
            sistOppdatert: ZonedDateTime,
            sikkerhetsnivaa: Int
    ) : this(
            null,
            eventTidspunkt,
            eventId,
            tekst,
            link,
            produsent,
            sistOppdatert,
            sikkerhetsnivaa
    )
}
