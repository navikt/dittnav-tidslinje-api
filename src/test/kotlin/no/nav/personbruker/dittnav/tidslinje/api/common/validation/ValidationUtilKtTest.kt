package no.nav.personbruker.dittnav.tidslinje.api.common.validation

import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.tidslinje.api.common.exception.FieldValidationException
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.jupiter.api.Test

class ValidationUtilKtTest {
    val field = "dummyValue"
    val fieldName = "dummyName"

    @Test
    fun `should throw exception if field is null`() {
        invoking {
            runBlocking {
                validateNonNullField(null, fieldName)
            }
        } `should throw` FieldValidationException::class
    }

    @Test
    fun `should return field when not null or blank`() {
        runBlocking {
            validateNonNullField(field, fieldName)
        } `should be equal to` field
    }

    @Test
    fun `do not allow too long field`() {
        val tooLongField = "L".repeat(101)
        invoking {
            runBlocking {
                validateMaxLength(tooLongField, fieldName, 100)
            }
        } `should throw` FieldValidationException::class
    }
}


