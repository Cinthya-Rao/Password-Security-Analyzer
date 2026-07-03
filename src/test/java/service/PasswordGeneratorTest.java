package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {
    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    void generatedPasswordUsesSelectedTypes() {
        String password = generator.generate(24, true, true, true, true, true);

        assertEquals(24, password.length());
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
        assertTrue(password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch)));
        assertFalse(password.matches(".*[0O1Il|`'].*"));
    }
}
