package chatapp.tests;

import chatapp.shared.models.chatpackages.GbcstPackage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackageTests {

    @Test
    public void gbcstConversionTest() {
        final String serverStr = "GBCST Group This is a message!";
        final String clientStr = "GBCST Sender Group This is a message!";
        GbcstPackage server = GbcstPackage.deserializeServer(serverStr);
        GbcstPackage client = GbcstPackage.deserializeClient(clientStr);
        assertEquals(server.toString(), serverStr);
        assertEquals(client.toString(), clientStr);
    }

}
