package model;

import org.junit.Test;
import exceptions.InvalidDataException;
import util.PasswordUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {

    @Test
    public void createsValidUserAndNormalisesEmail() {
        User user = new User("userteste", "  Teste@Example.COM ", PasswordUtils.hash("teste"));

        assertEquals("Pedro Silva", user.getName());
        assertEquals("pedro@example.com", user.getEmail());
        assertFalse(user.getPasswordHash().isEmpty());
        assertTrue(user.isEqualTo(new User("Outro Nome", "Teste@example.com", "x")));
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsShortName() {
        new User("ab", "teste@example.com", "teste");
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsNullName() {
        new User(null, "teste@example.com", "teste");
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsEmailWithoutAt() {
        new User("teste", "teste.example.com", "teste");
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsEmailWithoutDomain() {
        new User("teste", "teste@", "teste");
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsEmptyPasswordHash() {
        new User("teste", "teste@example.com", "   ");
    }

    @Test
    public void passwordHashIsDeterministicAndMatches() {
        String hash = PasswordUtils.hash("triptrail");

        assertEquals(hash, PasswordUtils.hash("triptrail"));
        assertTrue(PasswordUtils.matches("triptrail", hash));
        assertFalse(PasswordUtils.matches("outra", hash));
        assertFalse(PasswordUtils.matches("triptrail", null));
    }

    @Test
    public void passwordValidityRequiresSixCharacters() {
        assertTrue(PasswordUtils.isValid("123456"));
        assertFalse(PasswordUtils.isValid("12345"));
        assertFalse(PasswordUtils.isValid(null));
    }
}