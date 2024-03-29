package no.nav.personbruker.dittnav.tidslinje.api.innboks

fun toInnboksDTO(innboks: Innboks): InnboksDTO =
        innboks.let {
            InnboksDTO(
                    eventTidspunkt = it.eventTidspunkt,
                    eventId = it.eventId,
                    tekst = it.tekst,
                    link = it.link,
                    produsent = it.produsent,
                    sistOppdatert = it.sistOppdatert,
                    sikkerhetsnivaa = it.sikkerhetsnivaa,
                    eventtype = "Innboks"
            )
        }

fun toMaskedInnboksDTO(innboks: Innboks): InnboksDTO =
        innboks.let {
            val maskedInnboksDTO = toInnboksDTO(innboks)
            return maskedInnboksDTO.copy(tekst = "***", link = "***", produsent = "***")
        }
