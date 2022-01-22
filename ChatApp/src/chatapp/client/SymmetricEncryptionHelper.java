package chatapp.client;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SymmetricEncryptionHelper {
    private static final String AES = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";

    private byte[] initializationVector;
    private SecretKey secretKey;

    public void createSecrets() {
        try {
            secretKey = createAESKey();
            initializationVector = createInitializationVector();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public void setSecrets(SecretKey secretKey, byte[] initializationVector) {
        this.secretKey = secretKey;
        this.initializationVector = initializationVector;
    }

    public void setSecrets(byte[] secretKey, byte[] initializationVector) {
        setSecrets(convertByteArrayIntoSecretKey(secretKey), initializationVector);
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }


    public byte[] encrypt(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (!isSet()) throw new IllegalStateException("Can't encrypt without setting the secrets first");
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(byte[] encryptedValue) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (!isSet()) throw new IllegalStateException("Can't decrypt without setting the secrets first");
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] value = cipher.doFinal(encryptedValue);
        return new String(value);
    }

    private SecretKey createAESKey() throws NoSuchAlgorithmException {
        SecureRandom securerandom = new SecureRandom();
        KeyGenerator keygenerator = KeyGenerator.getInstance(AES);
        keygenerator.init(256, securerandom);
        return keygenerator.generateKey();
    }

    private static byte[] createInitializationVector() {
        byte[] initializationVector = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initializationVector);
        return initializationVector;
    }

    public boolean isSet() {
        return secretKey != null;
    }

    public SecretKey convertByteArrayIntoSecretKey(byte[] bytes) {
        return new SecretKeySpec(bytes, 0, bytes.length, AES);
    }
}
