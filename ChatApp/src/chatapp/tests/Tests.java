package chatapp.tests;

import chatapp.server.Config;
import chatapp.server.ServerApp;
import chatapp.shared.models.chatpackages.BcstPackage;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class Tests {

    @Test
    public void prototypeTest() {
        String serverStr = "BCST Group This is a message!";
        String clientStr = "BCST Sender Group This is a message!";
        BcstPackage server = BcstPackage.deserializeServer(serverStr);
        BcstPackage client = BcstPackage.deserializeClient(clientStr);
        assertEquals(server.toString(), serverStr);
        assertEquals(client.toString(), clientStr);
    }

    @Test
    public void test() throws IOException {
        ServerApp serverApp = new ServerApp();
        serverApp.start(Config.port);
    }

}
