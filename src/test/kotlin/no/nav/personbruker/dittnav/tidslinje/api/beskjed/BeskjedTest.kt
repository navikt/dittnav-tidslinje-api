package no.nav.personbruker.dittnav.tidslinje.api.beskjed

import org.amshove.kluent.`should contain`
import org.junit.jupiter.api.Test

class BeskjedTest {

    @Test
    fun `skal returnere maskerte data fra toString-metoden`() {
        val beskjed = createBeskjed("1", "1", "1", true)
        val beskjedAsString = beskjed.toString()
        beskjedAsString `should contain` "fodselsnummer=***"
        beskjedAsString `should contain` "tekst=***"
        beskjedAsString `should contain` "link=***"

    }
}