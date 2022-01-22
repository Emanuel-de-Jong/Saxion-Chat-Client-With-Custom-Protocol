package chatapp.tests;

import chatapp.client.AsymmetricEncryptionHelper;
import chatapp.client.ClientGlobals;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;

class AsymmetricEncryptionTests {
    AsymmetricEncryptionHelper helperA;
    AsymmetricEncryptionHelper helperB;
    Base64.Encoder encoder = Base64.getEncoder();
    Base64.Decoder decoder = Base64.getDecoder();

    @BeforeEach
    void beforeEach() {
        helperA = new AsymmetricEncryptionHelper();
        System.out.println("HelperA public key: " + encoder.encodeToString(helperA.getPublicKey().getEncoded()));
        helperB = new AsymmetricEncryptionHelper();
        System.out.println("HelperB public key: " + encoder.encodeToString(helperB.getPublicKey().getEncoded()));
    }

    @Test
    void encryptMessageAtoB() throws Exception {
        String message = "TEST MESSAGE";
        System.out.println("Message: " + message);
        byte[] encrypted = helperA.encrypt(message.getBytes(StandardCharsets.UTF_8),helperB.getPublicKey());
        System.out.println("Encrypted: " + encoder.encodeToString(encrypted));
        String decrypted = new String(helperB.decrypt(encrypted));
        System.out.println("Decrypted: " + decrypted);
        Assertions.assertEquals(message, decrypted);
    }

    @Test
    void encodeMessageAtoB() throws Exception {
        String message = "TEST MESSAGE";
        System.out.println("Message: " + message);
        byte[] encrypted = helperA.encrypt(message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encrypted: " + encoder.encodeToString(encrypted));
        String decrypted = new String(helperB.decrypt(encrypted, helperA.getPublicKey()));
        System.out.println("Decrypted: " + decrypted);
        Assertions.assertEquals(message, decrypted);
    }

    @Test
    void encryptMessageBtoA() throws Exception {
        String message = "TEST MESSAGE";
        System.out.println("Message: " + message);
        byte[] encrypted = helperB.encrypt(message.getBytes(StandardCharsets.UTF_8),helperA.getPublicKey());
        System.out.println("Encrypted: " + encoder.encodeToString(encrypted));
        String decrypted = new String(helperA.decrypt(encrypted));
        System.out.println("Decrypted: " + decrypted);
        Assertions.assertEquals(message, decrypted);
    }

    @Test
    void encryptMessageWithBase64key() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String message = "TEST MESSAGE";
        System.out.println("Message: " + message);
        String publicKeyHelperB = encoder.encodeToString(helperB.getPublicKey().getEncoded());
        PublicKey publicKeyB = helperA.convertByteArrayIntoPublicKey(decoder.decode(publicKeyHelperB));
        byte[] encrypted = helperA.encrypt(message.getBytes(StandardCharsets.UTF_8),publicKeyB);
        String decrypted = new String(helperB.decrypt(encrypted));
        Assertions.assertEquals(message,decrypted);
    }

    @Test
    void encodeMessageBtoA() throws Exception {
        String message = "TEST MESSAGE";
        System.out.println("Message: " + message);
        byte[] encrypted = helperB.encrypt(message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encrypted: " + encoder.encodeToString(encrypted));
        String decrypted = new String(helperA.decrypt(encrypted, helperB.getPublicKey()));
        System.out.println("Decrypted: " + decrypted);
        Assertions.assertEquals(message, decrypted);
    }

    @Test
    void encodeFail() throws Exception {
        String message = "TEST MESSAGE";
        System.out.println("Message: " + message);
        byte[] encrypted = helperB.encrypt(message.getBytes(StandardCharsets.UTF_8));
        Random random = new Random();
        random.nextBytes(encrypted);
        System.out.println("Encrypted: " + encoder.encodeToString(encrypted));
        try {
            String decrypted = new String(helperA.decrypt(encrypted, helperB.getPublicKey()));
            System.out.println("Decrypted: " + decrypted);
            Assertions.fail("failed to fail.");
        } catch (Exception ignored) {
        }
    }
}
