package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import java.time.ZoneId
import java.time.ZonedDateTime

fun createStatusoppdatering(eventId: String, fodselsnummer: String): Statusoppdatering {
    return Statusoppdatering(
            eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            fodselsnummer = fodselsnummer,
            eventId = eventId,
            grupperingsId = "Dok123",
            link = "https://nav.no/systemX/",
            produsent = "dittnav",
            sikkerhetsnivaa = 4,
            sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            statusGlobal = "Dummy global status",
            statusIntern = "Dummy intern status",
            sakstema = "Dummy sakstema"
    )
}
