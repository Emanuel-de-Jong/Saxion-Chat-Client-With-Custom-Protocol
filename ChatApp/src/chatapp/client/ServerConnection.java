package chatapp.client;

import chatapp.client.gui.AddGroupDialog;
import chatapp.client.gui.MainFrame;
import chatapp.client.interfaces.AddGroupDialogListener;
import chatapp.client.interfaces.MainFrameListener;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection implements MainFrameListener, AddGroupDialogListener {

    public static ArrayList<ServerConnectionListener> listeners = new ArrayList<>();

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerConnection() {
        try {
            MainFrame.listeners.add(this);
            AddGroupDialog.listeners.add(this);

            clientSocket = new Socket(Globals.ip, Globals.port);
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
        }
        else if (message.getGroupReceiver() != null) {
            BcstPackage bcstPackage = new BcstPackage(
                    message.getGroupReceiver().getName(),
                    message.getText());
            sendPackage(bcstPackage);
        }
    }

    @Override
    public void createGroup(String name) {
        sendPackage(new CgrpPackage(name));
    }

    @Override
    public void groupSelected(Group group) {
        sendPackage(new JgrpPackage(group.getName()));
    }

    private static class ServerHandler extends Thread {
        private Socket clientSocket;
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
                while ((packageStr = in.readLine()) != "false") {
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
