package no.nav.personbruker.dittnav.tidslinje.api.innboks

import java.time.ZoneId
import java.time.ZonedDateTime

fun createInnboks(eventId: String, fodselsnummer: String, aktiv: Boolean): Innboks {
    return Innboks(
            eventTidspunkt = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            fodselsnummer = fodselsnummer,
            eventId = eventId,
            grupperingsId = "Dok123",
            tekst = "Dette er en oppgave til brukeren",
            link = "https://nav.no/systemX/",
            produsent = "dittnav",
            sikkerhetsnivaa = 4,
            sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            aktiv = aktiv
    )
}

fun createInnboksWithEventTidspunkt(eventId: String, eventTidspunkt: ZonedDateTime): Innboks {
    return Innboks(
            eventTidspunkt = eventTidspunkt,
            fodselsnummer = "012",
            eventId = eventId,
            grupperingsId = "Dok123",
            tekst = "Dette er en oppgave til brukeren",
            link = "https://nav.no/systemX/",
            produsent = "dittnav",
            sikkerhetsnivaa = 4,
            sistOppdatert = ZonedDateTime.now(ZoneId.of("Europe/Oslo")),
            aktiv = true
    )
}

