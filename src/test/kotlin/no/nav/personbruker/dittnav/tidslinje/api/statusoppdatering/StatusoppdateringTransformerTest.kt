package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Test

class StatusoppdateringTransformerTest {

    @Test
    fun `should transform from statusoppdatering to statusoppdateringDTO`() {
        val statusoppdatering1 = createStatusoppdatering("1", "1")
        val statusoppdatering2 = createStatusoppdatering("2", "2")
        val statusoppdateringDTOList = listOf(statusoppdatering1, statusoppdatering2).map { toStatusoppdateringDTO(it) }
        val statusoppdateringDTO = statusoppdateringDTOList.first()

        statusoppdateringDTO.eventTidspunkt `should be` statusoppdatering1.eventTidspunkt
        statusoppdateringDTO.eventId `should be equal to` statusoppdatering1.eventId
        statusoppdateringDTO.link `should be equal to` statusoppdatering1.link
        statusoppdateringDTO.produsent!! `should be equal to` statusoppdatering1.produsent!!
        statusoppdateringDTO.sistOppdatert `should be` statusoppdatering1.sistOppdatert
        statusoppdateringDTO.sikkerhetsnivaa `should be` statusoppdatering1.sikkerhetsnivaa
        statusoppdateringDTO.statusGlobal `should be equal to` statusoppdatering1.statusGlobal
        statusoppdateringDTO.sakstema `should be equal to` statusoppdatering1.sakstema
    }

    @Test
    fun `should mask link and produsent`() {
        val statusoppdatering = createStatusoppdatering("1", "1")
        val statusoppdateringDTO = toMaskedStatusoppdateringDTO(statusoppdatering)

        statusoppdateringDTO.eventTidspunkt `should be` statusoppdatering.eventTidspunkt
        statusoppdateringDTO.eventId `should be equal to` statusoppdatering.eventId
        statusoppdateringDTO.link `should be equal to` "***"
        statusoppdateringDTO.produsent!! `should be equal to` "***"
        statusoppdateringDTO.sistOppdatert `should be` statusoppdatering.sistOppdatert
        statusoppdateringDTO.sikkerhetsnivaa `should be` statusoppdatering.sikkerhetsnivaa
        statusoppdateringDTO.statusGlobal `should be equal to` "***"
        statusoppdateringDTO.sakstema `should be equal to` "***"
    }
}
