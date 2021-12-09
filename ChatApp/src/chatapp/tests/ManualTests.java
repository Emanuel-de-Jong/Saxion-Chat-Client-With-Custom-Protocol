package chatapp.tests;

import chatapp.client.ClientApp;
import chatapp.client.ClientGlobals;
import chatapp.client.ServerConnection;
import chatapp.client.interfaces.GroupsListener;
import chatapp.server.ServerApp;
import chatapp.server.ServerGlobals;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.CgrpPackage;
import chatapp.shared.models.chatpackages.JgrpPackage;

public class ManualTests {

    public static void main(String[] args) throws Exception {
        new ManualTests();
    }

    public ManualTests() throws Exception {
        threeClients();
    }

    public void threeClients() throws Exception {
        // Configure env
        Globals.testing = true;

        // Start server
        ServerApp server = new ServerApp();
        server.start(ServerGlobals.port);

        // Create user1
        String userName1 = "user1";
        ClientApp client1 = new ClientApp(userName1);
        ClientGlobals globals1 = client1.getGlobals();
        ServerConnection connection1 = client1.getServerConnection();
        User user1 = globals1.currentUser;

        // Create user2
        String userName2 = "user2";
        ClientApp client2 = new ClientApp(userName2);
        ClientGlobals globals2 = client2.getGlobals();
        ServerConnection connection2 = client2.getServerConnection();
        User user2 = globals2.currentUser;

        // Create user3
        String userName3 = "user3";
        ClientApp client3 = new ClientApp(userName3);
        ClientGlobals globals3 = client3.getGlobals();
        ServerConnection connection3 = client3.getServerConnection();
        User user3 = globals3.currentUser;

        // User1 and 2 add each other
        globals1.users.get(userName2).setChatAdded(true);
        globals2.users.get(userName1).setChatAdded(true);

        // User1 and 2 message each other
        connection1.sendMessage(new Message("test " + userName1, user1, user2));
        connection2.sendMessage(new Message("test " + userName2, user2, user1));

        // All message public group
        Group publicGroup = globals1.groups.get(Globals.publicGroupName);
        connection1.sendMessage(new Message("test " + userName1, user1, publicGroup));
        connection2.sendMessage(new Message("test " + userName2, user2, publicGroup));
        connection3.sendMessage(new Message("test " + userName3, user3, publicGroup));

        // Create group1
        String groupName1 = "group1";
        connection1.sendPackage(new CgrpPackage(groupName1));
        Thread.sleep(500);
        Group group1 = globals1.groups.get(groupName1);

        // User1 and 3 join group1
        globals1.groups.get(groupName1).setJoined(true);
        globals3.groups.get(groupName1).setJoined(true);
        Thread.sleep(500);

        // User1 and 3 message group1
        connection1.sendMessage(new Message("test " + userName1, user1, group1));
        connection3.sendMessage(new Message("test " + userName3, user3, group1));
    }

}
