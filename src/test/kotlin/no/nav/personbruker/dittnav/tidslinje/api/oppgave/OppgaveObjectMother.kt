package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import java.time.ZoneId
import java.time.ZonedDateTime

fun createOppgave(eventId: String, fodselsnummer: String, aktiv: Boolean): Oppgave {
    return Oppgave(
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

fun createOppgaveWithEventTidspunkt(eventId: String, eventTidspunkt: ZonedDateTime): Oppgave {
    return Oppgave(
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
