package no.nav.personbruker.dittnav.tidslinje.api.done

object DoneObjectMother {

    fun createDone(uid: String, eventId: String): DoneDto {
        return DoneDto(
                uid = uid,
                eventId = eventId)
    }

}