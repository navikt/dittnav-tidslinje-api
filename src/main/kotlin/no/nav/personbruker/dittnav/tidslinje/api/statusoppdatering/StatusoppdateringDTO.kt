package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.Brukernotifikasjon
import java.time.ZonedDateTime

data class StatusoppdateringDTO(
        val produsent: String?,
        val eventId: String,
        override val eventTidspunkt: ZonedDateTime,
        val fodselsnummer: String,
        val grupperingsId: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val statusGlobal: String,
        val statusIntern: String?,
        val sakstema: String
) : Brukernotifikasjon {
    constructor(
            eventId: String,
            eventTidspunkt: ZonedDateTime,
            fodselsnummer: String,
            grupperingsId: String,
            link: String,
            sikkerhetsnivaa: Int,
            sistOppdatert: ZonedDateTime,
            statusGlobal: String,
            statusIntern: String?,
            sakstema: String
    ) : this(
            null,
            eventId,
            eventTidspunkt,
            fodselsnummer,
            grupperingsId,
            link,
            sikkerhetsnivaa,
            sistOppdatert,
            statusGlobal,
            statusIntern,
            sakstema
    )
}
