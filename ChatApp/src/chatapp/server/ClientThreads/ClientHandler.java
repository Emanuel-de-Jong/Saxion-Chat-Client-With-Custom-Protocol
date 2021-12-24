package chatapp.server.ClientThreads;

import chatapp.server.ServerGlobals;
import chatapp.server.models.AuthUser;
import chatapp.server.models.Client;
import chatapp.shared.Globals;
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
            sendPackageOther(new DscndPackage(client.getName()));

            clientPackageHandler.interrupt();
            clientPinger.interrupt();
            clientIdleChecker.interrupt();

            globals.users.remove(client.getName());
            for (Group group : globals.groups.values()) {
                group.removeUser(client.getUser());
            }
            globals.clients.remove(client);

            client.getSocket().close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void connect(String username, String password) throws IOException {
        if (!username.matches(Globals.ALLOWED_CHARACTERS)) {
            sendPackage(new ErPackage(2, "Username has an invalid format " +
                    "(only characters, numbers and underscores are allowed)"));
            return;
        }

        User user;
        if (password != null) {
            AuthUser authenticatedUser = globals.authenticatedUsers.get(username);
            if (authenticatedUser == null || !authenticatedUser.validate(password)) {
                sendPackage(new ErPackage(25, "Username or Password is incorrect"));
                return;
            }
            user = authenticatedUser;
        } else {
            if (globals.authenticatedUsers.containsKey(username) ||
                    globals.users.containsKey(username)) {
                sendPackage(new ErPackage(24, "Username already exists"));
                return;
            }
            user = new User(username, false, globals);
        }

        globals.users.put(username, user);
        client.setUser(user);

        sendPackage(new OkPackage(username));
        sendPackageOther(new UsrPackage(username, user.isVerified()));

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
        for (User u : globals.users.values()) {
            Socket clientSocket = globals.clients.getByName(u.getName()).getSocket();
            sendPackage(clientSocket, chatPackage);
        }
    }

    public void sendPackageOther(ChatPackage chatPackage) throws IOException {
        for (User u : globals.users.values()) {
            if (u.equals(client.getUser())) continue;
            Socket clientSocket = globals.clients.getByName(u.getName()).getSocket();
            sendPackage(clientSocket, chatPackage);
        }
    }

    public void sendPackageAllInGroup(Group group, ChatPackage chatPackage) throws IOException {
        for (User u : group.getUsers().values()) {
            Socket clientSocket = globals.clients.getByName(u.getName()).getSocket();
            sendPackage(clientSocket, chatPackage);
        }
    }

}
