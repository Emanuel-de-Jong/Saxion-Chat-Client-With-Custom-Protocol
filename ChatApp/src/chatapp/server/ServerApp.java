package chatapp.server;

import chatapp.client.ClientGlobals;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;

public class ServerApp {

    public static void main(String[] args) {
        ServerApp serverApp = new ServerApp();
        serverApp.start(ServerGlobals.port);
    }

    public HashMap<String, Socket> clientSockets = new HashMap<>();

    private final HashMap<String, User> users = new HashMap<>();
    private final HashMap<String, Group> groups = new HashMap<>();
    private ServerSocket serverSocket;
    private ServerGlobals globals;

    public void start(int port) {
        try {
            globals = new ServerGlobals();

            serverSocket = new ServerSocket(port);

            groups.put(Globals.publicGroupName, new Group(Globals.publicGroupName, globals));

            new Thread(() -> {
                try {
                    while (true) {
                        Socket clientSocket = serverSocket.accept();

                        new ClientHandler(clientSocket).start();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private User user;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String packageStr;
                while (!(packageStr = in.readLine()).equals("false")) {
                    ChatPackage chatPackage = ChatPackageHelper.deserialize(packageStr, false);
                    System.out.println(chatPackage);

                    switch (chatPackage.getType()) {
                        case CONN:
                            ConnPackage connPackage = (ConnPackage) chatPackage;
                            user = new User(connPackage.getUserName(), globals);
                            users.put(user.getName(), user);
                            clientSockets.put(user.getName(), clientSocket);
                            sendPackageAll(new UsrPackage(user.getName()));
                            break;
                        case MSG:
                            MsgPackage msgPackage = (MsgPackage) chatPackage;
                            msgPackage.setSender(user.getName());
                            sendPackage(clientSockets.get(msgPackage.getReceiver()), msgPackage);
                            break;
                        case BCST:
                            BcstPackage bcstPackage = (BcstPackage) chatPackage;
                            if (groups.get(bcstPackage.getGroupName()).hasUser(user)) {
                                bcstPackage.setSender(user.getName());
                                sendPackageAllInGroup(bcstPackage.getGroupName(), bcstPackage);
                            }
                            break;
                        case CGRP:
                            CgrpPackage cgrpPackage = (CgrpPackage) chatPackage;
                            Group group = new Group(cgrpPackage.getGroupName(), globals);
                            groups.put(group.getName(), group);
                            sendPackageAll(new GrpPackage(group.getName()));
                            break;
                        case JGRP:
                            JgrpPackage jgrpPackage = (JgrpPackage) chatPackage;
                            groups.get(jgrpPackage.getGroupName()).addUser(user);
                            jgrpPackage.setUserName(user.getName());
                            sendPackageAll(jgrpPackage);
                            break;
                        case USRS:
                            UsrsPackage usrsPackage = (UsrsPackage) chatPackage;
                            usrsPackage.setUserNames(users.keySet().toArray(new String[0]));
                            sendPackage(clientSocket, usrsPackage);
                            break;
                        case GRPS:
                            GrpsPackage grpsPackage = (GrpsPackage) chatPackage;
                            grpsPackage.setGroupNames(groups.keySet().toArray(new String[0]));
                            sendPackage(clientSocket, grpsPackage);
                            break;
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void sendPackage(Socket clientSocket, ChatPackage chatPackage) throws IOException {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(chatPackage);
        }

        private void sendPackageAll(ChatPackage chatPackage) throws IOException {
            for (User u : users.values()) {
                sendPackage(clientSockets.get(u.getName()), chatPackage);
            }
        }

        private void sendPackageAllInGroup(String groupName, ChatPackage chatPackage) throws IOException {
            Collection<User> sendTo;
            if (groupName.equals(Globals.publicGroupName)) {
                sendTo = groups.get(groupName).getUsers().values();
            } else {
                sendTo = users.values();
            }

            for (User u : sendTo) {
                sendPackage(clientSockets.get(u.getName()), chatPackage);
            }
        }
    }

}
