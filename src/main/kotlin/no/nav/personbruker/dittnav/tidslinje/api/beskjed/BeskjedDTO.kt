@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.personbruker.dittnav.tidslinje.api.beskjed

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.tidslinje.api.brukernotifikasjon.BrukernotifikasjonDTO
import no.nav.personbruker.dittnav.tidslinje.api.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
@SerialName("Beskjed")
data class BeskjedDTO(
        val uid: String?,
        override val eventTidspunkt: ZonedDateTime,
        val eventId: String,
        val tekst: String,
        val link: String,
        val produsent: String?,
        val sistOppdatert: ZonedDateTime,
        val sikkerhetsnivaa: Int,
        override val eventtype: String
) : BrukernotifikasjonDTO {
    constructor(
            eventTidspunkt: ZonedDateTime,
            eventId: String,
            tekst: String,
            link: String,
            produsent: String?,
            sistOppdatert: ZonedDateTime,
            sikkerhetsnivaa: Int,
            eventtype: String
    ) : this(
            null,
            eventTidspunkt,
            eventId,
            tekst,
            link,
            produsent,
            sistOppdatert,
            sikkerhetsnivaa,
            eventtype
    )
}
