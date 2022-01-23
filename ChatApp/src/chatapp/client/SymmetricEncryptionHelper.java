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

    /**
     * generate the secret sauce of this encryption
     */
    public void createSecrets() {
        try {
            secretKey = createAESKey();
            initializationVector = createInitializationVector();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * set the seccrets.
     * @param secretKey
     * @param initializationVector
     */
    public void setSecrets(SecretKey secretKey, byte[] initializationVector) {
        this.secretKey = secretKey;
        this.initializationVector = initializationVector;
    }

    /**
     * set the secrets if you only have bytes
     * @param secretKey
     * @param initializationVector
     */
    public void setSecrets(byte[] secretKey, byte[] initializationVector) {
        setSecrets(convertByteArrayIntoSecretKey(secretKey), initializationVector);
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    /**
     * encrypt a string
     * make sure the secrets are set
     * @param value
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public byte[] encrypt(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (!isSet()) throw new IllegalStateException("Can't encrypt without setting the secrets first");
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * decrypt a string
     * make sure secrets are set
     * @param encryptedValue
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(byte[] encryptedValue) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (!isSet()) throw new IllegalStateException("Can't decrypt without setting the secrets first");
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] value = cipher.doFinal(encryptedValue);
        return new String(value);
    }

    /**
     * generate an aes key
     * @return
     * @throws NoSuchAlgorithmException
     */
    private SecretKey createAESKey() throws NoSuchAlgorithmException {
        SecureRandom securerandom = new SecureRandom();
        KeyGenerator keygenerator = KeyGenerator.getInstance(AES);
        keygenerator.init(256, securerandom);
        return keygenerator.generateKey();
    }

    /**
     * generate a Initialization vector (secure random)
     * @return
     */
    private static byte[] createInitializationVector() {
        byte[] initializationVector = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initializationVector);
        return initializationVector;
    }


    public boolean isSet() {
        return secretKey != null;
    }

    /**
     * turn a byte arraay into a secret key
     * @param bytes
     * @return
     */
    public SecretKey convertByteArrayIntoSecretKey(byte[] bytes) {
        return new SecretKeySpec(bytes, 0, bytes.length, AES);
    }
}
