package chatapp.server;

import chatapp.server.models.Client;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientPackageHandler extends Thread {

    private User user;
    private Client client;
    private PrintWriter out;
    private BufferedReader in;

    private ServerGlobals globals;


    public ClientPackageHandler(Client client, ServerGlobals globals) {
        this.client = client;
        this.globals = globals;

        client.setPackageHandler(this);
    }


    public void run() {
        try {
            out = new PrintWriter(client.getSocket().getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));

            String packageStr;
            while (!(packageStr = in.readLine()).equals("false")) {
                ChatPackage chatPackage = ChatPackageHelper.deserialize(packageStr, false);
                System.out.println(chatPackage);

                switch (chatPackage.getType()) {
                    case PONG -> Pong();
                    case CONN -> Conn((ConnPackage) chatPackage);
                    case MSG -> Msg((MsgPackage) chatPackage);
                    case BCST -> Bcst((BcstPackage) chatPackage);
                    case CGRP -> Cgrp((CgrpPackage) chatPackage);
                    case JGRP -> Jgrp((JgrpPackage) chatPackage);
                    case USRS -> Usrs((UsrsPackage) chatPackage);
                    case GRPS -> Grps((GrpsPackage) chatPackage);
                }
            }

            in.close();
            out.close();
            client.getSocket().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendPackage(Socket clientSocket, ChatPackage chatPackage) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(chatPackage);
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

    private void Pong() {
        client.getPinger().setTimeSincePong(System.currentTimeMillis());
    }

    private void Conn(ConnPackage connPackage) throws IOException {
        String username = connPackage.getUserName();
        if (username.contains(" ")) {
            sendPackage(client.getSocket(), new ErPackage(123,"Username cannot contain spaces"));
            return;
        }
        if (connPackage.hasPassword()) {
            var authenticatedUser = globals.authenticatedUsers.get(username);
            var password = connPackage.getPassword();
            if (authenticatedUser != null && authenticatedUser.validate(password)) {
                user = authenticatedUser;
                System.out.println("Login: " + user);
            } else {
                sendPackage(client.getSocket(), new ErPackage(125, "Username or Password is incorrect."));
                System.out.println("Username or Password is incorrect.");
                return;
            }
        } else {
            if (globals.authenticatedUsers.containsKey(username)) {
                sendPackage(client.getSocket(), new ErPackage(124, "Username already belongs to an authenticated user."));
                System.out.println("Username already belongs to an authenticated user.");
                return;
            }
            user = new User(username, globals);
        }

        globals.users.put(username, user);
        client.setName(username);
        client.getPinger().start();
        sendPackageAll(new UsrPackage(username));
    }

    private void Msg(MsgPackage msgPackage) throws IOException {
        msgPackage.setSender(user.getName());
        sendPackage(client.getSocket(), msgPackage);
        Socket clientSocket = globals.clients.getByName(msgPackage.getReceiver()).getSocket();
        sendPackage(clientSocket, msgPackage);

    }

    private void Bcst(BcstPackage bcstPackage) throws IOException {
        if (globals.groups.get(bcstPackage.getGroupName()).hasUser(user)) {
            bcstPackage.setSender(user.getName());
            sendPackageAllInGroup(bcstPackage.getGroupName(), bcstPackage);
        }
    }

    private void Cgrp(CgrpPackage cgrpPackage) throws IOException {
        Group group = new Group(cgrpPackage.getGroupName(), globals);
        globals.groups.put(group.getName(), group);
        sendPackageAll(new GrpPackage(group.getName()));
    }

    private void Jgrp(JgrpPackage jgrpPackage) throws IOException {
        globals.groups.get(jgrpPackage.getGroupName()).addUser(user);
        jgrpPackage.setUserName(user.getName());
        sendPackageAll(jgrpPackage);
    }

    private void Usrs(UsrsPackage usrsPackage) throws IOException {
        usrsPackage.setUserNames(globals.users.keySet().toArray(new String[0]));
        sendPackage(client.getSocket(), usrsPackage);
    }

    private void Grps(GrpsPackage grpsPackage) throws IOException {
        grpsPackage.setGroupNames(globals.groups.keySet().toArray(new String[0]));
        sendPackage(client.getSocket(), grpsPackage);
    }

}
