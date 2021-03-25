@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.BrukernotifikasjonDTO
import no.nav.personbruker.dittnav.tidslinje.api.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
@SerialName("Statusoppdatering")
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
        override val eventtype: String
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
            eventtype: String
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
            eventtype
    )
}
