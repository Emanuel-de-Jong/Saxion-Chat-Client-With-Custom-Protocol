package chatapp.server.clientthreads;

import chatapp.server.ServerGlobals;
import chatapp.server.models.Client;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Group;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ClientPackageHandler extends Thread {

    private final ClientHandler clientHandler;
    private final ClientPinger clientPinger;
    private final ClientIdleChecker clientIdleChecker;

    private final Client client;
    private final ServerGlobals globals;


    public ClientPackageHandler(ClientHandler clientHandler, ClientPinger clientPinger,
                                ClientIdleChecker clientIdleChecker, Client client, ServerGlobals globals) {
        this.clientHandler = clientHandler;
        this.clientPinger = clientPinger;
        this.clientIdleChecker = clientIdleChecker;
        this.client = client;
        this.globals = globals;
    }


    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));

            String packageStr;
            while (!Thread.currentThread().isInterrupted() &&
                    !(packageStr = in.readLine()).equals("false")) {
                ChatPackage chatPackage = ChatPackageHelper.deserialize(packageStr, false);
                if (chatPackage == null) {
                    clientHandler.sendPackage(new ErPackage(30, "Invalid (number of) values in: " + packageStr));
                    continue;
                }

                if (chatPackage.getType() != ChatPackageType.PONG) {
                    System.out.println("SP: " + chatPackage);
                }

                if (!isLoggedIn() && chatPackage.getType() != ChatPackageType.CONN) {
                    clientHandler.sendPackage(new ErPackage(3, "Please log in first"));
                    continue;
                }

                switch (chatPackage.getType()) {
                    case CONN -> conn((ConnPackage) chatPackage);
                    case USRS -> usrs((UsrsPackage) chatPackage);
                    case GRPS -> grps((GrpsPackage) chatPackage);
                    case CGRP -> cgrp((CgrpPackage) chatPackage);
                    case JGRP -> jgrp((JgrpPackage) chatPackage);
                    case LGRP -> lgrp((LgrpPackage) chatPackage);
                    case MSG -> msg((MsgPackage) chatPackage);
                    case BCST -> bcst((BcstPackage) chatPackage);
                    case GBCST -> gbcst((GbcstPackage) chatPackage);
                    case PONG -> pong();
                    case QUIT -> quit();
                    default -> clientHandler.sendPackage(new ErPackage(0, "Unknown command"));
                }
            }
        } catch (SocketException | NullPointerException ex) {
            clientHandler.interrupt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean isLoggedIn() {
        return client.getUser() != null;
    }


    private void conn(ConnPackage connPackage) throws IOException {
        if (isLoggedIn()) {
            clientHandler.sendPackage(new ErPackage(1, "User already logged in"));
            return;
        }
        clientHandler.connect(connPackage.getUserName(), connPackage.getPassword());
    }

    private void usrs(UsrsPackage usrsPackage) throws IOException {
        globals.users.forEach((userName, user) -> usrsPackage.addUserName(userName, user.isVerified()));
        clientHandler.sendPackage(usrsPackage);
    }

    private void grps(GrpsPackage grpsPackage) throws IOException {
        grpsPackage.setGroupNames(globals.groups.keySet().toArray(new String[0]));
        clientHandler.sendPackage(grpsPackage);
    }

    private void cgrp(CgrpPackage cgrpPackage) throws IOException {
        String groupName = cgrpPackage.getGroupName();
        if (!groupName.matches(Globals.ALLOWED_CHARACTERS)) {
            clientHandler.sendPackage(new ErPackage(4, "Group name has an invalid format " +
                    "(only characters, numbers and underscores are allowed)"));
            return;
        }
        Group group = new Group(groupName, globals);
        globals.groups.put(groupName, group);
        clientHandler.sendPackageAll(new GrpPackage(groupName));
    }

    private void jgrp(JgrpPackage jgrpPackage) throws IOException {
        globals.groups.get(jgrpPackage.getGroupName()).addUser(client.getUser());
        clientIdleChecker.addGroup(jgrpPackage.getGroupName());

        jgrpPackage.setUserName(client.getName());
        clientHandler.sendPackageAll(jgrpPackage);
    }

    private void lgrp(LgrpPackage lgrpPackage) throws IOException {
        globals.groups.get(lgrpPackage.getGroupName()).removeUser(client.getUser());
        clientIdleChecker.removeGroup(lgrpPackage.getGroupName());

        lgrpPackage.setUserName(client.getName());
        clientHandler.sendPackageAll(lgrpPackage);
    }

    private void msg(MsgPackage msgPackage) throws IOException {
        msgPackage.setSender(client.getName());
        clientHandler.sendPackage(msgPackage);
        Socket clientSocket = globals.clients.getByName(msgPackage.getReceiver()).getSocket();
        clientHandler.sendPackage(clientSocket, msgPackage);

    }

    private void bcst(BcstPackage bcstPackage) throws IOException {
        Group group = globals.groups.get(Globals.PUBLIC_GROUP_NAME);

        clientHandler.sendPackage(new OkPackage(bcstPackage.toString()));

        bcstPackage.setSender(client.getName());
        clientHandler.sendPackageAllInGroup(group, bcstPackage);
    }

    private void gbcst(GbcstPackage gbcstPackage) throws IOException {
        Group group = globals.groups.get(gbcstPackage.getGroupName());

        if (!group.hasUser(client.getUser())) {
            clientHandler.sendPackage(new ErPackage(15, "You are not in the group"));
            return;
        }

        clientIdleChecker.updateGroup(group.getName());

        clientHandler.sendPackage(new OkPackage(gbcstPackage.toString()));

        gbcstPackage.setSender(client.getName());
        clientHandler.sendPackageAllInGroup(group, gbcstPackage);
    }

    private void pong() {
        clientPinger.setLastPongTime(System.currentTimeMillis());
    }

    private void quit() throws IOException {
        clientHandler.sendPackage(new OkPackage("Goodbye"));
        clientHandler.interrupt();
    }

}
