package night.mission.intimate

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordValidatorTest {

    @Test
    fun testValidPassword() {
        assertTrue(PasswordValidator.validate("sex"))
    }

    @Test
    fun testInvalidPassword() {
        assertFalse(PasswordValidator.validate("SEX"))
        assertFalse(PasswordValidator.validate("wrong"))
        assertFalse(PasswordValidator.validate(""))
        assertFalse(PasswordValidator.validate("sex1"))
    }
}
