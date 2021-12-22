package chatapp.server.models;

import chatapp.shared.Globals;
import chatapp.shared.models.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HexFormat;

public class AuthUser extends User {

    private static final byte[] PEPPER = HexFormat.of().parseHex("5fc6a31698e6306b656b7840f258a915");

    private final byte[] hash;
    private final byte[] salt;

    public AuthUser(String name, String password) {
        super(name,true,null);
        salt = generateSalt();
        hash = generateHash(password,salt);
    }

    public AuthUser(String name, byte[] hash, byte[] salt){
        super(name, true, null);
        this.salt = salt;
        this.hash = hash;
    }

    public static byte[] generateHash(String password, byte[] salt) {
        if (salt == null || password == null || password.length() == 0) return null;
        ByteArrayOutputStream saltPepperStream = new ByteArrayOutputStream( );
        try {
            saltPepperStream.write(salt);
            saltPepperStream.write(PEPPER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] saltPepper = saltPepperStream.toByteArray();

        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltPepper, 65536, 128);
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public boolean validate(String password) {
        var newHash = generateHash(password,salt);
        if (newHash == null || hash == null) return false;
        return Arrays.equals(newHash, hash);
    }
}
