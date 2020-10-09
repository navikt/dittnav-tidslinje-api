package no.nav.personbruker.dittnav.tidslinje.api.common.validation

import no.nav.personbruker.dittnav.tidslinje.api.common.exception.FieldValidationException

fun validateNonNullFieldMaxLength(field: String?, fieldName: String, maxLength: Int): String {
    return validateMaxLength(validateNonNullField(field, fieldName), fieldName, maxLength)
}

fun validateNonNullField(field: String?, fieldName: String): String {
    if (field.isNullOrBlank()) {
        val fve = FieldValidationException("$fieldName var null eller tomt.")
        fve.addContext("nullOrBlank", fieldName)
        throw fve
    }
    return field
}

fun validateMaxLength(field: String, fieldName: String, maxLength: Int): String {
    if (field.length > maxLength) {
        val fve = FieldValidationException("Feltet $fieldName kan ikke inneholde mer enn $maxLength tegn.")
        fve.addContext("rejectedFieldValueLength", field.length)
        fve.addContext("rejectedFieldValue", field)
        throw fve
    }
    return field
}