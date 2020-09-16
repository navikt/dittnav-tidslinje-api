package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

class StatusoppdateringTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val Statusoppdatering = createStatusoppdatering("1", "1")
        val StatusoppdateringAsString = Statusoppdatering.toString()
        StatusoppdateringAsString `should contain` "fodselsnummer=***"
        StatusoppdateringAsString `should contain` "link=***"

    }
}
