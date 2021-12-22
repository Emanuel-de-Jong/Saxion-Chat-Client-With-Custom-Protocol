package chatapp.client;

import chatapp.client.interfaces.AddGroupDialogListener;
import chatapp.client.interfaces.MainFrameListener;
import chatapp.client.interfaces.SystemHelperListener;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection implements MainFrameListener, AddGroupDialogListener, GroupListener,
        SystemHelperListener {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final ClientGlobals globals;

    public ServerConnection(ClientGlobals globals) {
        this.globals = globals;

        try {
            globals.clientListeners.mainFrame.add(this);
            globals.clientListeners.addGroupDialog.add(this);
            globals.clientListeners.systemHelper.add(this);
            globals.listeners.group.add(this);

            clientSocket = new Socket(ClientGlobals.ip, Globals.port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            new ServerHandler(clientSocket).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendPackage(ChatPackage chatPackage) {
        out.println(chatPackage);
    }

    @Override
    public void sendMessage(Message message) {
        System.out.println("C: ServerConnection sendMessage " + message);
        if (message.getUserReceiver() != null) {
            MsgPackage msgPackage = new MsgPackage(
                    message.getUserReceiver().getName(),
                    message.getText());
            sendPackage(msgPackage);
        } else if (message.getGroupReceiver() != null) {
            BcstPackage bcstPackage = new BcstPackage(
                    message.getGroupReceiver().getName(),
                    message.getText());
            sendPackage(bcstPackage);
        }
    }

    @Override
    public void createGroup(String name) {
        System.out.println("C: ServerConnection createGroup " + name);
        sendPackage(new CgrpPackage(name));
    }

    @Override
    public void joinedSet(Group group, boolean joined) {
        System.out.println("C: ServerConnection joinedSet " + group + " " + joined);
        if (joined) {
            sendPackage(new JgrpPackage(group.getName()));
        } else {
            sendPackage(new LgrpPackage(group.getName()));
        }
    }

    @Override
    public void messageAdded(Group group, Message message) {}

    @Override
    public void exiting() {
        System.out.println("C: ServerConnection exiting");
        sendPackage(new QuitPackage());
    }

    private class ServerHandler extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ServerHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String packageStr;
                while (!(packageStr = in.readLine()).equals("false")) {
                    ChatPackage chatPackage = ChatPackageHelper.deserialize(packageStr, true);
                    if (chatPackage.getType() != ChatPackageType.PING) {
                        System.out.println(chatPackage);
                    }

                    switch (chatPackage.getType()) {
                        case BCST:
                            BcstPackage bcstPackage = (BcstPackage) chatPackage;
                            if (!globals.groups.containsKey(bcstPackage.getGroupName())) {
                                bcstPackage.setMessage(bcstPackage.getGroupName() + " " + bcstPackage.getMessage());
                                bcstPackage.setGroupName(Globals.publicGroupName);
                            }
                            globals.clientListeners.serverConnection.forEach(l -> l.chatPackageReceived(bcstPackage));
                        case PING:
                            sendPackage(new PongPackage());
                            break;
                        case DSCN:
                            globals.systemHelper.restart();
                        default:
                            globals.clientListeners.serverConnection.forEach(l -> l.chatPackageReceived(chatPackage));
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
