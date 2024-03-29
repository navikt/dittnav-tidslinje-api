package no.nav.personbruker.dittnav.tidslinje.api.innboks

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Test

class InnboksTransformerTest {

    @Test
    fun `should transform from Innboks to InnboksDTO`() {
        val innboks1 = createInnboks("1", "1", true)
        val innboks2 = createInnboks("2", "2", true)
        val innboksDTOList = listOf(innboks1, innboks2).map { toInnboksDTO(it) }
        val innboksDTO = innboksDTOList.first()

        innboksDTO.eventTidspunkt `should be` innboks1.eventTidspunkt
        innboksDTO.eventId `should be equal to` innboks1.eventId
        innboksDTO.tekst `should be equal to` innboks1.tekst
        innboksDTO.link `should be equal to` innboks1.link
        innboksDTO.produsent!! `should be equal to` innboks1.produsent!!
        innboksDTO.sistOppdatert `should be` innboks1.sistOppdatert
        innboksDTO.sikkerhetsnivaa `should be` innboks1.sikkerhetsnivaa
        innboksDTO.eventtype `should be equal to` "Innboks"
    }

    @Test
    fun `should mask tekst, link and produsent`() {
        val innboks = createInnboks("1", "1", true)
        val innboksDTO = toMaskedInnboksDTO(innboks)
        innboksDTO.eventTidspunkt `should be` innboks.eventTidspunkt
        innboksDTO.eventId `should be equal to` innboks.eventId
        innboksDTO.tekst `should be equal to` "***"
        innboksDTO.link `should be equal to` "***"
        innboksDTO.produsent!! `should be equal to` "***"
        innboksDTO.sistOppdatert `should be` innboks.sistOppdatert
        innboksDTO.sikkerhetsnivaa `should be` innboks.sikkerhetsnivaa
        innboksDTO.eventtype `should be equal to` "Innboks"
    }
}
