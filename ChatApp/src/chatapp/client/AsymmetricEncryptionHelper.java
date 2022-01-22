package chatapp.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AsymmetricEncryptionHelper {
    private static final String RSA = "RSA";

    private KeyPair keyPair;
    private Base64.Encoder base64enc = Base64.getEncoder();

    public AsymmetricEncryptionHelper() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(2048, secureRandom);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }



    public byte[] encrypt(byte[] value) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return encrypt(value, keyPair.getPrivate());
    }

    public byte[] encrypt(byte[] value, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(value);
    }

    public byte[] decrypt(byte[] encryptedText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return decrypt(encryptedText, keyPair.getPrivate());
    }

    public byte[] decrypt(byte[] encryptedText, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedText);
    }

    public PublicKey convertByteArrayIntoPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(bytes));
    }
}
