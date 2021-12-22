package chatapp.tests;

import chatapp.shared.models.chatpackages.BcstPackage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackageTests {

    @Test
    public void bcstConversionTest() {
        final String serverStr = "BCST Group This is a message!";
        final String clientStr = "BCST Sender Group This is a message!";
        BcstPackage server = BcstPackage.deserializeServer(serverStr);
        BcstPackage client = BcstPackage.deserializeClient(clientStr);
        assertEquals(server.toString(), serverStr);
        assertEquals(client.toString(), clientStr);
    }

}
