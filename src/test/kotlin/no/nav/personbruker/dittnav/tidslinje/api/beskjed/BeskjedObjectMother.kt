package no.nav.personbruker.dittnav.tidslinje.api.beskjed

import java.time.ZoneId
import java.time.ZonedDateTime

fun createBeskjed(eventId: String, fodselsnummer: String, uid: String, aktiv: Boolean): Beskjed {
    return Beskjed(
            uid = uid,
            eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            fodselsnummer = fodselsnummer,
            eventId = eventId,
            grupperingsId = "Dok123",
            tekst = "Dette er beskjed til brukeren",
            link = "https://nav.no/systemX/",
            produsent = "dittnav",
            sikkerhetsnivaa = 4,
            sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            aktiv = aktiv
    )
}

fun createBeskjedWithEventTidspunkt(eventId: String, uid: String, eventTidspunkt: ZonedDateTime): Beskjed {
    return Beskjed(
            uid = uid,
            eventTidspunkt = eventTidspunkt,
            fodselsnummer = "012",
            eventId = eventId,
            grupperingsId = "Dok123",
            tekst = "Dette er beskjed til brukeren",
            link = "https://nav.no/systemX/",
            produsent = "dittnav",
            sikkerhetsnivaa = 4,
            sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            aktiv = true
    )
}
