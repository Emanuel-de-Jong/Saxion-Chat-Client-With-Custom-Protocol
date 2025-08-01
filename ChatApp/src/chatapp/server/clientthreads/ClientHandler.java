package chatapp.server.clientthreads;

import chatapp.server.ServerGlobals;
import chatapp.server.models.AuthUser;
import chatapp.server.models.Client;
import chatapp.shared.Globals;
import chatapp.shared.enums.Flag;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Client client;

    private final ClientPinger clientPinger;
    private final ClientIdleChecker clientIdleChecker;
    private final ClientPackageHandler clientPackageHandler;

    private final ServerGlobals globals;

    /**
     * a thread that handles everything for the clients.
     * @param client
     * @param globals
     */
    public ClientHandler(Client client, ServerGlobals globals) {
        this.client = client;
        this.globals = globals;

        clientPinger = new ClientPinger(this);
        clientIdleChecker = new ClientIdleChecker(this);
        clientPackageHandler = new ClientPackageHandler(this, clientPinger, clientIdleChecker, client, globals);
    }

    public void run() {
        try {
            clientPackageHandler.start();

            sendPackage(new InfoPackage("Welcome to the server " + String.format("%.1f", ServerGlobals.VERSION)));

            while (!Thread.currentThread().isInterrupted()) {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        removeClient();
    }


    private void removeClient() {
        try {
            clientPackageHandler.interrupt();
            clientPinger.interrupt();
            clientIdleChecker.interrupt();

            String userName = client.getName();
            if (userName != null) {
                sendPackageOther(new DscndPackage(userName));

                globals.users.remove(userName);
                for (Group group : globals.groups.values()) {
                    if (group.hasUser(userName)) {
                        sendPackageGroup(group, new LgrpPackage(group.getName(), userName));
                        group.removeUser(userName);
                    }
                }
            }

            globals.clients.remove(client);
            client.getSocket().close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void connect(String username, String password) throws IOException {
        if (!username.matches(Globals.ALLOWED_CHARACTERS)) {
            sendPackage(ErPackage.USER_NAME_INVALID);
            return;
        }

        User user;
        if (password != null) {
            AuthUser authenticatedUser = globals.authenticatedUsers.get(username);
            if (authenticatedUser == null || !authenticatedUser.validate(password)) {
                sendPackage(ErPackage.LOG_IN_INVALID);
                return;
            }
            if (globals.users.containsKey(username)) {
                sendPackage(ErPackage.USER_NAME_EXISTS);
                return;
            }
            user = authenticatedUser;
        } else {
            if (globals.authenticatedUsers.containsKey(username)) {
                sendPackage(ErPackage.USER_NAME_EXISTS);
                return;
            }
            if (globals.users.containsKey(username)) {
                sendPackage(ErPackage.ALREADY_LOGGED_IN);
                return;
            }
            user = new User(username, false, globals);
        }

        globals.users.put(username, user);
        client.setUser(user);

        sendPackage(new OkPackage(username));
        sendPackageOther(new UsrPackage(username, user.isVerified()), Flag.GetNewUsers);

        clientPinger.start();
        clientIdleChecker.start();
    }


    public void sendPackage(Socket clientSocket, ChatPackage chatPackage) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(chatPackage);
    }

    public void sendPackage(ChatPackage chatPackage) throws IOException {
        sendPackage(client.getSocket(), chatPackage);
    }

    public void sendPackageAll(ChatPackage chatPackage) throws IOException {
        sendPackageAll(chatPackage, null);
    }

    public void sendPackageAll(ChatPackage chatPackage, Flag flag) throws IOException {
        for (User u : globals.users.values()) {
            Client client = globals.clients.getByName(u.getName());
            if (flag != null && !client.containsFlag(flag)) continue;
            sendPackage(client.getSocket(), chatPackage);
        }
    }

    public void sendPackageOther(ChatPackage chatPackage) throws IOException {
        sendPackageOther(chatPackage, null);
    }

    public void sendPackageOther(ChatPackage chatPackage, Flag flag) throws IOException {
        for (User u : globals.users.values()) {
            if (u.equals(client.getUser())) continue;
            Client client = globals.clients.getByName(u.getName());
            if (flag != null && !client.containsFlag(flag)) continue;
            sendPackage(client.getSocket(), chatPackage);
        }
    }

    public void sendPackageGroup(Group group, ChatPackage chatPackage) throws IOException {
        sendPackageGroup(group, chatPackage, null);
    }

    public void sendPackageGroup(Group group, ChatPackage chatPackage, Flag flag) throws IOException {
        for (User u : group.getUsers().values()) {
            Client client = globals.clients.getByName(u.getName());
            if (flag != null && !client.containsFlag(flag)) continue;
            sendPackage(client.getSocket(), chatPackage);
        }
    }

}
