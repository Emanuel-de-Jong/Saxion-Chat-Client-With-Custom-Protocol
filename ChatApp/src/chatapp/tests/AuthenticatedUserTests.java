package chatapp.tests;

import chatapp.server.models.AuthUser;
import org.junit.Test;

public class AuthenticatedUserTests {
    @Test
    public void generateHashSaltPair() {
        byte[] salt = AuthUser.generateSalt();
        byte[] hash = AuthUser.generateHash("authUser1",salt);
        System.out.println("Salt: " + encodeHexString(salt));
        assert hash != null;
        System.out.println("Hash: " + encodeHexString(hash));
    }

    public String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
}
