package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.BrukernotifikasjonDTO
import java.time.ZonedDateTime

data class StatusoppdateringDTO(
        val produsent: String?,
        val eventId: String,
        override val eventTidspunkt: ZonedDateTime,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val statusGlobal: String,
        val statusIntern: String?,
        val sakstema: String,
        val type: String
) : BrukernotifikasjonDTO {
    constructor(
            eventId: String,
            eventTidspunkt: ZonedDateTime,
            link: String,
            sikkerhetsnivaa: Int,
            sistOppdatert: ZonedDateTime,
            statusGlobal: String,
            statusIntern: String?,
            sakstema: String,
            type: String
    ) : this(
            null,
            eventId,
            eventTidspunkt,
            link,
            sikkerhetsnivaa,
            sistOppdatert,
            statusGlobal,
            statusIntern,
            sakstema,
            type
    )
}
