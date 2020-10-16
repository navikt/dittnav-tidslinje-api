package no.nav.personbruker.dittnav.tidslinje.api.oppgave

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.ConsumeEventException
import no.nav.personbruker.dittnav.tidslinje.api.common.InnloggetBrukerObjectMother
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

class OppgaveServiceTest {

    val oppgaveConsumer = mockk<OppgaveConsumer>()
    val oppgaveService = OppgaveService(oppgaveConsumer)
    var innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker()
    val grupperingsid = "Dok123"
    val produsent = "dittnav"
    val fodselsnummer = "1"

    @Test
    fun `should return list of OppgaveDTO when Events are received`() {
        val oppgave1 = createOppgave(eventId = "1", fodselsnummer = fodselsnummer, aktiv = true)
        val oppgave2 = createOppgave(eventId = "2", fodselsnummer = "2", aktiv = true)
        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(oppgave1, oppgave2)
        runBlocking {
            val oppgaveList = oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsid, produsent)
            oppgaveList.size `should be equal to` 2
        }
    }

    @Test
    fun `should mask events with security level higher than current user`() {
        var oppgave = createOppgave(eventId = "1", fodselsnummer = fodselsnummer, aktiv = true)
        oppgave = oppgave.copy(sikkerhetsnivaa = 4)
        innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(ident = fodselsnummer, innloggingsnivaa = 3)
        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(oppgave)
        runBlocking {
            val oppgaveList = oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsid, produsent)
            val oppgaveDTO = oppgaveList.first()
            oppgaveDTO.tekst `should be equal to` "***"
            oppgaveDTO.link `should be equal to` "***"
            oppgaveDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should not mask events with security level lower than current user`() {
        var oppgave = createOppgave(eventId = "1", fodselsnummer = fodselsnummer, aktiv = true)
        oppgave = oppgave.copy(sikkerhetsnivaa = 3)
        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(oppgave)
        runBlocking {
            val oppgaveList = oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsid, produsent)
            val oppgaveDTO = oppgaveList.first()
            oppgaveDTO.tekst `should be equal to` oppgave.tekst
            oppgaveDTO.link `should be equal to` oppgave.link
            oppgaveDTO.sikkerhetsnivaa `should be equal to` 3
        }
    }

    @Test
    fun `should not mask events with security level equal than current user`() {
        val oppgave = createOppgave(eventId = "1", fodselsnummer = fodselsnummer, aktiv = true)
        coEvery { oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent) } returns listOf(oppgave)
        runBlocking {
            val oppgaveList = oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsid, produsent)
            val oppgaveDTO = oppgaveList.first()
            oppgaveDTO.tekst `should be equal to` oppgave.tekst
            oppgaveDTO.link `should be equal to` oppgave.link
            oppgaveDTO.sikkerhetsnivaa `should be equal to` 4
        }
    }

    @Test
    fun `should throw exception if fetching events fail`() {
        coEvery {
            oppgaveConsumer.getExternalEvents(innloggetBruker, grupperingsid, produsent)
        } throws Exception("error")
        invoking {
            runBlocking {
                oppgaveService.getOppgaveEvents(innloggetBruker, grupperingsid, produsent)
            }
        } `should throw` ConsumeEventException::class
    }

}
