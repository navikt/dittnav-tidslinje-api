@file:UseSerializers(ZonedDateTimeSerializer::class)
package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.personbruker.dittnav.tidslinje.api.common.serializer.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
data class Statusoppdatering(
        val produsent: String?,
        val eventId: String,
        val eventTidspunkt: ZonedDateTime,
        val fodselsnummer: String,
        val grupperingsId: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val statusGlobal: String,
        val statusIntern: String?,
        val sakstema: String
) {
    override fun toString(): String {
        return "Statusoppdatering(" +
                "eventId=$eventId, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "link=***, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "statusGlobal=$statusGlobal, " +
                "statusIntern=$statusIntern, " +
                "sakstema=$sakstema, "
    }
}
