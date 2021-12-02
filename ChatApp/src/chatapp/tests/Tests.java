package chatapp.tests;

import chatapp.client.ClientApp;
import chatapp.client.Globals;
import chatapp.client.ServerConnection;
import chatapp.server.Config;
import chatapp.server.ServerApp;
import chatapp.shared.models.Message;
import chatapp.shared.models.chatpackages.BcstPackage;
import org.junit.Test;

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

    public static void main(String[] args) {
        new Tests().manualTest();
    }
    public void manualTest() {
        ServerApp server = new ServerApp();
        server.start(Config.port);

        String userName1 = "user1";
        ClientApp client1 = new ClientApp(userName1);
        Globals globals1 = client1.getGlobals();
        ServerConnection connection1 = client1.getServerConnection();
        String userName2 = "user2";
        ClientApp client2 = new ClientApp(userName2);
        Globals globals2 = client2.getGlobals();
        ServerConnection connection2 = client1.getServerConnection();

        connection1.sendMessage(new Message("test " + userName1, globals1.currentUser, globals2.currentUser));
        connection2.sendMessage(new Message("test " + userName2, globals2.currentUser, globals1.currentUser));
    }

}
