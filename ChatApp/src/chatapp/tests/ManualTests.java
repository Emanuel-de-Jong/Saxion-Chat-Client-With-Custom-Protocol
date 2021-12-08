package chatapp.tests;

import chatapp.client.ClientApp;
import chatapp.client.ClientGlobals;
import chatapp.client.ServerConnection;
import chatapp.server.ServerApp;
import chatapp.server.ServerConfig;
import chatapp.shared.models.Message;

public class ManualTests {

    public static void main(String[] args) {
        new ManualTests();
    }

    public ManualTests() {
        twoClients();
    }

    public void twoClients() {
        ServerApp server = new ServerApp();
        server.start(ServerConfig.port);

        String userName1 = "user1";
        ClientApp client1 = new ClientApp(userName1);
        ClientGlobals globals1 = client1.getGlobals();
        ServerConnection connection1 = client1.getServerConnection();
        String userName2 = "user2";
        ClientApp client2 = new ClientApp(userName2);
        ClientGlobals globals2 = client2.getGlobals();
        ServerConnection connection2 = client1.getServerConnection();

        connection1.sendMessage(new Message("test " + userName1, globals2.currentUser));
        connection2.sendMessage(new Message("test " + userName2, globals1.currentUser));
    }

}
