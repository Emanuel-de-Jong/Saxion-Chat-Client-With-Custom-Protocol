package chatapp.client;

import chatapp.client.gui.AddGroupDialog;
import chatapp.client.gui.MainFrame;
import chatapp.client.interfaces.AddGroupDialogListener;
import chatapp.client.interfaces.MainFrameListener;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.ListenerHelper;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.interfaces.Listener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection implements MainFrameListener, AddGroupDialogListener, GroupListener {

    public static ArrayList<ServerConnectionListener> listeners = new ArrayList<>();

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerConnection() {
        try {
            MainFrame.listeners.add(this);
            AddGroupDialog.listeners.add(this);
            Group.listeners.add(this);

            clientSocket = new Socket(ClientGlobals.ip, ClientGlobals.port);
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
        System.out.println("ServerConnection messageSent " + message);
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
        System.out.println("ServerConnection createGroup " + name);
        sendPackage(new CgrpPackage(name));
    }

    @Override
    public void joinedSet(Group group, boolean joined) {
        System.out.println("ServerConnection joinedSet " + group + " " + joined);
        if (joined) {
            sendPackage(new JgrpPackage(group.getName()));
        } else {
            sendPackage(new LgrpPackage(group.getName()));
        }
    }

    @Override
    public void messageAdded(Group group, Message message) {}

    private static class ServerHandler extends Thread {
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
                    System.out.println(chatPackage);

                    switch (chatPackage.getType()) {
                        default:
                            listeners.forEach(l -> l.chatPackageReceived(chatPackage));
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
