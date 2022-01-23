package chatapp.tests;

import chatapp.server.models.AuthUser;
import chatapp.server.storage.AuthUsersStorage;
import org.junit.Test;

import java.util.HexFormat;

import static org.junit.Assert.*;


public class AuthUserTests {

    @Test
    public void generateHashSaltPair() {
        byte[] salt = AuthUser.generateSalt();
        byte[] hash = AuthUser.generateHash("manuel123", salt);
        System.out.println("Salt: " + encodeHexString(salt));
        System.out.println("Hash: " + encodeHexString(hash));
        assertNotNull(hash);
    }

    public String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuffer.append(HexFormat.of().toHexDigits(b));
        }
        return hexStringBuffer.toString();
    }

    @Test
    public void authUserValidation() {
        var user = new AuthUser("username", "password123");
        var validate = user.validate("password123");
        assertTrue(validate);
    }

    @Test
    public void defaultAccounts() {
        var authUsers = new AuthUsersStorage();
        assertTrue(authUsers.get("authUser1").validate("authUser1"));
        assertTrue(authUsers.get("authUser2").validate("authUser2"));
        assertTrue(authUsers.get("authUser3").validate("authUser3"));
        assertFalse(authUsers.get("authUser4").validate("authUser3"));
        assertFalse(authUsers.get("authUser1").validate("authUser2"));
    }

}
