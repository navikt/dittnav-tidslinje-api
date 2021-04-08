package no.nav.personbruker.dittnav.tidslinje.api.common

import no.nav.personbruker.dittnav.tidslinje.api.config.Systembruker

object SystembrukerObjectMother {

    fun createSystembruker(): Systembruker {
        return Systembruker("dittnav")
    }
}
