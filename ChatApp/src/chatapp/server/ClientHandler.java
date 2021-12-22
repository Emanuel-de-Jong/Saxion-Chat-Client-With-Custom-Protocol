package chatapp.server;

import chatapp.server.models.Client;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private User user;
    private final Client client;
    private PrintWriter out;
    private BufferedReader in;

    private ClientPinger clientPinger;
    private ClientIdleChecker clientIdleChecker;

    private final ServerGlobals globals;


    public ClientHandler(Client client, ServerGlobals globals) {
        this.client = client;
        this.globals = globals;

        clientPinger = new ClientPinger(this, globals);
        clientIdleChecker = new ClientIdleChecker(this, globals);
    }


    public void run() {
        try {
            out = new PrintWriter(client.getSocket().getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));

            sendPackage(new InfoPackage("Welcome to the server"));

            String packageStr;
            while (!Thread.currentThread().isInterrupted() &&
                    !(packageStr = in.readLine()).equals("false")) {
                ChatPackage chatPackage = ChatPackageHelper.deserialize(packageStr, false);
                if (chatPackage.getType() != ChatPackageType.PONG) {
                    System.out.println("SP: " + chatPackage);
                }

                if (!isLoggedIn() && chatPackage.getType() != ChatPackageType.CONN) {
                    sendPackage(new ErPackage(3, "Please log in first"));
                    return;
                }

                switch (chatPackage.getType()) {
                    case PONG -> pong();
                    case CONN -> Conn((ConnPackage) chatPackage);
                    case MSG -> msg((MsgPackage) chatPackage);
                    case BCST -> bcst((BcstPackage) chatPackage);
                    case CGRP -> cgrp((CgrpPackage) chatPackage);
                    case JGRP -> jgrp((JgrpPackage) chatPackage);
                    case LGRP -> lgrp((LgrpPackage) chatPackage);
                    case USRS -> usrs((UsrsPackage) chatPackage);
                    case GRPS -> grps((GrpsPackage) chatPackage);
                    case QUIT -> quit();
                    default -> sendPackage(new ErPackage(0, "Unknown command"));
                }
            }

            in.close();
            out.close();
            client.getSocket().close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        clientPinger.interrupt();
        clientIdleChecker.interrupt();
        removeFromServer();

        try {
            sendPackageAll(new DscndPackage(user.getName()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void removeFromServer() {
        globals.users.remove(user.getName());
        for (Group group : globals.groups.values()) {
            group.removeUser(user);
        }
        globals.clients.remove(client);
    }

    private boolean isLoggedIn() {
        return client.getName() != null;
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

    public void sendPackageAllInGroup(String groupName, ChatPackage chatPackage) throws IOException {
        for (User u : globals.groups.get(groupName).getUsers().values()) {
            Socket clientSocket = globals.clients.getByName(u.getName()).getSocket();
            sendPackage(clientSocket, chatPackage);
        }
    }


    private void pong() {
        clientPinger.setLastPongTime(System.currentTimeMillis());
    }

    private void Conn(ConnPackage connPackage) throws IOException {
        if (!isLoggedIn()) {
            sendPackage(new ErPackage(1, "User already logged in"));
            return;
        }

        String username = connPackage.getUserName();
        if (username.contains(" ") || username.contains("*")) {
            sendPackage(new ErPackage(2, "Username has an invalid format " +
                    "(only characters, numbers and underscores are allowed)"));
            return;
        }
        if (connPackage.hasPassword()) {
            var authenticatedUser = globals.authenticatedUsers.get(username);
            var password = connPackage.getPassword();
            if (authenticatedUser != null && authenticatedUser.validate(password)) {
                user = authenticatedUser;
            } else {
                sendPackage(new ErPackage(25, "Username or Password is incorrect."));
                return;
            }
        } else {
            if (globals.authenticatedUsers.containsKey(username)) {
                sendPackage(new ErPackage(24, "Username already belongs to an authenticated user."));
                return;
            }
            user = new User(username, false, globals);
        }
        globals.users.put(username, user);
        client.setName(username);

        clientPinger.start();
        clientIdleChecker.start();

        sendPackage(new OkPackage(username));
        sendPackageAll(new UsrPackage(username, user.isVerified()));
    }

    private void msg(MsgPackage msgPackage) throws IOException {
        msgPackage.setSender(user.getName());
        sendPackage(msgPackage);
        Socket clientSocket = globals.clients.getByName(msgPackage.getReceiver()).getSocket();
        sendPackage(clientSocket, msgPackage);

    }

    private void bcst(BcstPackage bcstPackage) throws IOException {
        Group group = globals.groups.get(bcstPackage.getGroupName());
        if (group == null) {
            group = globals.groups.get(Globals.publicGroupName);
        }

        if (!group.hasUser(user)) return;

        clientIdleChecker.updateGroup(group.getName());

        sendPackage(new OkPackage(bcstPackage.toString()));

        bcstPackage.setSender(user.getName());
        sendPackageAllInGroup(group.getName(), bcstPackage);
    }

    private void cgrp(CgrpPackage cgrpPackage) throws IOException {
        Group group = new Group(cgrpPackage.getGroupName(), globals);
        globals.groups.put(group.getName(), group);
        sendPackageAll(new GrpPackage(group.getName()));
    }

    private void jgrp(JgrpPackage jgrpPackage) throws IOException {
        globals.groups.get(jgrpPackage.getGroupName()).addUser(user);
        clientIdleChecker.addGroup(jgrpPackage.getGroupName());

        jgrpPackage.setUserName(user.getName());
        sendPackageAll(jgrpPackage);
    }

    private void lgrp(LgrpPackage lgrpPackage) throws IOException {
        globals.groups.get(lgrpPackage.getGroupName()).removeUser(user);
        clientIdleChecker.removeGroup(lgrpPackage.getGroupName());

        lgrpPackage.setUserName(user.getName());
        sendPackageAll(lgrpPackage);
    }

    private void usrs(UsrsPackage usrsPackage) throws IOException {
        globals.users.forEach((userName, user) -> {
            usrsPackage.addUserName(userName, user.isVerified());
        });
        sendPackage(usrsPackage);
    }

    private void grps(GrpsPackage grpsPackage) throws IOException {
        grpsPackage.setGroupNames(globals.groups.keySet().toArray(new String[0]));
        sendPackage(grpsPackage);
    }

    public void quit() throws IOException {
        sendPackage(new OkPackage("Goodbye"));
        this.interrupt();
    }

}
