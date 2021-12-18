package chatapp.tests;

import chatapp.shared.models.chatpackages.BcstPackage;
import org.junit.Test;

import static org.junit.Assert.*;

public class PackageTests {

    @Test
    public void bcstConversionTest() {
        String serverStr = "BCST Group This is a message!";
        String clientStr = "BCST Sender Group This is a message!";
        BcstPackage server = BcstPackage.deserializeServer(serverStr);
        BcstPackage client = BcstPackage.deserializeClient(clientStr);
        assertEquals(server.toString(), serverStr);
        assertEquals(client.toString(), clientStr);
    }

}
