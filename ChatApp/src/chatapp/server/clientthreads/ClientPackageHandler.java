package chatapp.server.clientthreads;

import chatapp.server.ServerGlobals;
import chatapp.server.models.Client;
import chatapp.server.models.FileTransfer;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Group;
import chatapp.shared.models.chatpackages.*;
import chatapp.shared.models.chatpackages.filetransfer.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                ChatPackage chatPackage;
                try {
                    chatPackage = ChatPackageHelper.deserialize(packageStr, false);
                } catch (IllegalArgumentException ex) {
                    clientHandler.sendPackage(ErPackage.UNKNOWN);
                    continue;
                }

                if (chatPackage == null) {
                    clientHandler.sendPackage(ErPackage.PACKAGE_INVALID);
                    System.out.println(packageStr);
                    continue;
                }
                globals.systemHelper.log(chatPackage.toString(), true);

                if (!isConnected() && chatPackage.getType() != ChatPackageType.CONN) {
                    clientHandler.sendPackage(ErPackage.NOT_LOGGED_IN);
                    continue;
                }

                switch (chatPackage.getType()) {
                    case CONN -> conn((ConnPackage) chatPackage);
                    case FLAG -> flag((FlagPackage) chatPackage);
                    case USRS -> usrs((UsrsPackage) chatPackage);
                    case GRPS -> grps((GrpsPackage) chatPackage);
                    case CGRP -> cgrp((CgrpPackage) chatPackage);
                    case JGRP -> jgrp((JgrpPackage) chatPackage);
                    case LGRP -> lgrp((LgrpPackage) chatPackage);
                    case MSG -> msg((MsgPackage) chatPackage);
                    case BCST -> bcst((BcstPackage) chatPackage);
                    case GBCST -> gbcst((GbcstPackage) chatPackage);

                    case UPRQ -> uprq((UprqPackage) chatPackage);
                    case DNAC -> dnac((DnacPackage) chatPackage);

                    case PONG -> pong();
                    case QUIT -> quit();
                }
            }
        } catch (SocketException | NullPointerException ex) {
            clientHandler.interrupt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




    private boolean isConnected() {
        return client.getUser() != null;
    }


    private void conn(ConnPackage connPackage) throws IOException {
        if (isConnected()) {
            clientHandler.sendPackage(ErPackage.ALREADY_LOGGED_IN);
            return;
        }
        clientHandler.connect(connPackage.getUserName(), connPackage.getPassword());
    }

    private void flag(FlagPackage flagPackage) {
        client.addFlag(flagPackage.getFlag());
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
            clientHandler.sendPackage(ErPackage.GROUP_NAME_INVALID);
            return;
        }
        Group group = new Group(groupName, globals);
        globals.groups.put(groupName, group);
        clientHandler.sendPackageAll(new GrpPackage(groupName));
    }

    private void jgrp(JgrpPackage jgrpPackage) throws IOException {
        Group group = globals.groups.get(jgrpPackage.getGroupName());
        group.addUser(client.getUser());
        clientIdleChecker.addGroup(jgrpPackage.getGroupName());

        jgrpPackage.setUserName(client.getName());
        clientHandler.sendPackageGroup(group, jgrpPackage);
    }

    private void lgrp(LgrpPackage lgrpPackage) throws IOException {
        Group group = globals.groups.get(lgrpPackage.getGroupName());
        group.removeUser(client.getUser());
        clientIdleChecker.removeGroup(lgrpPackage.getGroupName());

        lgrpPackage.setUserName(client.getName());
        clientHandler.sendPackageGroup(group, lgrpPackage);
    }

    private void msg(MsgPackage msgPackage) throws IOException {
        msgPackage.setSender(client.getName());
        clientHandler.sendPackage(msgPackage);
        Socket clientSocket = globals.clients.getByName(msgPackage.getReceiver()).getSocket();
        clientHandler.sendPackage(clientSocket, msgPackage);

    }

    private void bcst(BcstPackage bcstPackage) throws IOException {
        clientHandler.sendPackage(new OkPackage(bcstPackage.toString()));
        bcstPackage.setSender(client.getName());
        clientHandler.sendPackageOther(bcstPackage);
    }

    private void gbcst(GbcstPackage gbcstPackage) throws IOException {
        Group group = globals.groups.get(gbcstPackage.getGroupName());

        if (!group.hasUser(client.getName())) {
            clientHandler.sendPackage(ErPackage.NOT_IN_GROUP);
            return;
        }

        clientIdleChecker.updateGroup(group.getName());

        clientHandler.sendPackage(new OkPackage(gbcstPackage.toString()));

        gbcstPackage.setSender(client.getName());
        clientHandler.sendPackageGroup(group, gbcstPackage);
    }

    private void uprq(UprqPackage uprqPackage) throws IOException {
        if (uprqPackage.getFileSize() > 1073741824) {
            clientHandler.sendPackage(ErPackage.FILE_TRANSFER_INCORRECT);
            return;
        }
        Client targetClient = globals.clients.getByName(uprqPackage.getUser());
        Socket targetSocket = targetClient.getSocket();
        FileTransferHandler fileTransferHandler = globals.fileTransferHandlers.get(uprqPackage.getConnection());
        if (fileTransferHandler == null) {
            clientHandler.sendPackage(ErPackage.FILE_TRANSFER_INCORRECT);
            return;
        }
        FileTransfer fileTransfer = new FileTransfer(
                client.getUser(),
                fileTransferHandler,
                uprqPackage.getFileName(),
                uprqPackage.getHash(),
                uprqPackage.getFileSize(),
                globals.clients.getByName(uprqPackage.getUser()).getUser()
        );
        globals.fileTransfers.add(fileTransfer);
        clientHandler.sendPackage(targetSocket,new DnrqPackage(client.getName(),uprqPackage.getFileName(),uprqPackage.getFileSize(),uprqPackage.getHash()));
        new Thread(() -> {
            try {
                Thread.sleep(5 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            globals.fileTransfers.remove(fileTransfer);
        }).start();
    }

    private void dnac(DnacPackage dnacPackage) throws IOException {
        Client targetClient = globals.clients.getByName(dnacPackage.getUser());
        Socket targetSocket = targetClient.getSocket();

        List<FileTransfer> fileTransferList = globals.fileTransfers.stream().filter(fileTransfer -> (
                    Arrays.equals(fileTransfer.getHash(),dnacPackage.getHash()) &&
                    fileTransfer.getSender().getName().equals(dnacPackage.getUser())
                )).collect(Collectors.toList());

        if (fileTransferList.size() <= 0) {
            return;
        }

        FileTransfer fileTransfer = fileTransferList.get(fileTransferList.size()-1);
        fileTransfer.setReceiverFileTransferHandler(globals.fileTransferHandlers.get(dnacPackage.getConnection()));
        fileTransfer.getSenderFileTransferHandler().setTarget(fileTransfer.getReceiverFileTransferHandler());

        clientHandler.sendPackage(targetSocket,new UpacPackage(client.getName()));
    }

    private void pong() {
        clientPinger.setLastPongTime(System.currentTimeMillis());
    }

    private void quit() throws IOException {
        clientHandler.sendPackage(new OkPackage("Goodbye"));
        clientHandler.interrupt();
    }

}
