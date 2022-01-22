package chatapp.tests;

import chatapp.client.SymmetricEncryptionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;

class SymmetricEncryptionTests {
    SymmetricEncryptionHelper helperA;
    SymmetricEncryptionHelper helperB;
    Base64.Encoder encoder = Base64.getEncoder();

    @BeforeEach
    void beforeEach() {
        helperA = new SymmetricEncryptionHelper();
        helperB = new SymmetricEncryptionHelper();
    }

    @Test
    void encryptMessage() throws Exception {
        String message = "secret message";
        System.out.println("Message: " + message);
        helperA.createSecrets();
        SecretKey secretKey = helperA.getSecretKey();
        byte[] initvector = helperA.getInitializationVector();
        helperB.setSecrets(secretKey,initvector);
        System.out.println("Secretkey: " + encoder.encodeToString(secretKey.getEncoded()));
        byte[] encrypted = helperA.encrypt(message);
        String decrypted = helperB.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
        Assertions.assertEquals(message,decrypted);
    }

}
