package no.nav.personbruker.dittnav.tidslinje.api.statusoppdatering

fun toStatusoppdateringDTO(statusoppdatering: Statusoppdatering): StatusoppdateringDTO =
        statusoppdatering.let {
            StatusoppdateringDTO(
                    produsent = it.produsent,
                    eventId = it.eventId,
                    eventTidspunkt = it.eventTidspunkt,
                    link = it.link,
                    sikkerhetsnivaa = it.sikkerhetsnivaa,
                    sistOppdatert = it.sistOppdatert,
                    statusGlobal = it.statusGlobal,
                    statusIntern = it.statusIntern,
                    sakstema = it.sakstema,
                    eventtype = "Statusoppdatering"
            )
        }

fun toMaskedStatusoppdateringDTO(statusoppdatering: Statusoppdatering): StatusoppdateringDTO =
        statusoppdatering.let {
            val maskedStatusoppdateringDTO = toStatusoppdateringDTO(statusoppdatering)
            return maskedStatusoppdateringDTO.copy(link = "***", produsent = "***", statusGlobal = "***", statusIntern = "***", sakstema = "***")
        }
