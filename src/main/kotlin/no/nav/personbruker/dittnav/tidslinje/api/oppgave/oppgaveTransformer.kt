package no.nav.personbruker.dittnav.tidslinje.api.oppgave

fun toOppgaveDTO(inbound: Oppgave): OppgaveDTO =
        inbound.let {
            OppgaveDTO(
                    eventTidspunkt = it.eventTidspunkt,
                    eventId = it.eventId,
                    tekst = it.tekst,
                    link = it.link,
                    produsent = it.produsent,
                    sistOppdatert = it.sistOppdatert,
                    sikkerhetsnivaa = it.sikkerhetsnivaa,
                    eventtype = "Oppgave"
            )
        }

fun toMaskedOppgaveDTO(oppgave: Oppgave): OppgaveDTO =
        oppgave.let {
            val maskedOppgaveDTO = toOppgaveDTO(oppgave)
            return maskedOppgaveDTO.copy(tekst = "***", link = "***", produsent = "***")
        }
