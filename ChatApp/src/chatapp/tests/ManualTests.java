package chatapp.tests;

import chatapp.client.ClientApp;
import chatapp.server.ServerApp;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.chatpackages.CgrpPackage;

import java.awt.*;
import java.util.ArrayList;

public class ManualTests {

    private static final String userName = "user";
    private static final String groupName = "group";
    private static final Point[] frameLocations = {
            new Point(0, 65),
            new Point(1120, 65),
            new Point(0, 550),
            new Point(1120, 550)
    };

    private final ArrayList<ClientApp> clients = new ArrayList<>();
    private final ArrayList<Group> groups = new ArrayList<>();
    private final Group publicGroup = new Group(Globals.publicGroupName, null);


    public static void main(String[] args) throws Exception {
        new ManualTests();
    }

    public ManualTests() throws Exception {
        // Configure env
        Globals.testing = true;

        // Start server
        ServerApp server = new ServerApp();
        server.start(Globals.port);

//        twoClients();

//        twoClientsWithData();

//        threeClients();

        threeClientsWithData();

//        fourClients();
    }


    public void twoClients() {
        // Create user1 and 2
        createClients(2);
    }

    public void twoClientsWithData() throws Exception {
        // Create user1 and 2
        createClients(2);
        ClientApp client1 = clients.get(0);
        ClientApp client2 = clients.get(1);

        // User1 and 2 add each other
        addUserChat(client1, client2);

        // User1 and 2 message each other
        messageUser(client1, client2);
        messageUser(client2, client1);

        // User1 and 2 message publicGroup
        messageGroup(client1, publicGroup);
        messageGroup(client2, publicGroup);

        // Create group1
        createGroups(client1, 1);
        Group group1 = groups.get(0);

        // User1 and 2 join group1
        joinGroup(client1, group1);
        joinGroup(client2, group1);

        // User1 and 2 message group1
        messageGroup(client1, group1);
        messageGroup(client2, group1);
    }


    public void threeClients() {
        // Create user1, 2 and 3
        createClients(3);
    }

    public void threeClientsWithData() throws Exception {
        // Create user1, 2 and 3
        createClients(3);
        ClientApp client1 = clients.get(0);
        ClientApp client2 = clients.get(1);
        ClientApp client3 = clients.get(2);

        // User1 and 2 add each other
        addUserChat(client1, client2);

        // User1 and 2 message each other
        messageUser(client1, client2);
        messageUser(client2, client1);

        // User1, 2 and 3 message publicGroup
        messageGroup(client1, publicGroup);
        messageGroup(client2, publicGroup);
        messageGroup(client3, publicGroup);

        // Create group1
        createGroups(client1, 1);
        Group group1 = groups.get(0);

        // User1 and 3 join group1
        joinGroup(client1, group1);
        joinGroup(client3, group1);

        // User1 and 3 message group1
        messageGroup(client1, group1);
        messageGroup(client3, group1);
    }

    public void fourClients() throws Exception {
        createClients(4);
    }


    public void createClients(int amount) {
        for (int i = 0; i < amount; i++) {
            ClientApp client = new ClientApp(userName + (clients.size() + 1));
            clients.add(client);

            if (frameLocations.length >= clients.size()) {
                client.getMainFrame().getFrame().setLocation(frameLocations[clients.size() - 1]);
            }
        }
    }

    public void createGroups(ClientApp client, int amount) throws Exception {
        for (int i = 0; i < amount; i++) {
            String groupName = ManualTests.groupName + (groups.size() + 1);
            client.getServerConnection().sendPackage(new CgrpPackage(groupName));
            groups.add(new Group(groupName, null));
        }
        Thread.sleep(200);
    }

    public void addUserChat(ClientApp client, ClientApp target) {
        client.getGlobals().users.get(target.getGlobals().currentUser.getName()).setChatAdded(true);
    }

    public void joinGroup(ClientApp client, Group target) throws Exception {
        client.getGlobals().groups.get(target.getName()).setJoined(true);
        Thread.sleep(200);
    }

    public void messageUser(ClientApp client, ClientApp target) {
        client.getServerConnection().sendMessage(new Message("test",
                client.getGlobals().currentUser,
                target.getGlobals().currentUser));
    }

    public void messageGroup(ClientApp client, Group target) {
        client.getServerConnection().sendMessage(new Message("test",
                client.getGlobals().currentUser,
                target));
    }

}
