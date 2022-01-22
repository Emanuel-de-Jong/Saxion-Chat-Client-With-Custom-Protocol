package chatapp.client;

import chatapp.client.interfaces.*;
import chatapp.shared.Globals;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;
import chatapp.shared.models.chatpackages.encryption.MsgsPackage;
import chatapp.shared.models.chatpackages.encryption.RqpkPackage;
import chatapp.shared.models.chatpackages.filetransfer.DnacPackage;
import chatapp.shared.models.chatpackages.filetransfer.DnrqPackage;
import chatapp.shared.models.chatpackages.filetransfer.UprqPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;

public class ServerConnection implements ChatPanelListener, AddGroupDialogListener, GroupListener,
        SystemHelperListener, UploadListener, DownloadListener {


    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ServerHandler serverHandler;
    private final ClientGlobals globals;

    public ServerConnection(ClientGlobals globals) throws Exception {
        this.globals = globals;

        globals.clientListeners.chatPanel.add(this);
        globals.clientListeners.addGroupDialog.add(this);
        globals.clientListeners.uploads.add(this);
        globals.clientListeners.downloads.add(this);
        globals.listeners.systemHelper.add(this);
        globals.listeners.group.add(this);

        globals.systemHelper.log("Connecting to " + Globals.IP + ':' + Globals.PORT);
        clientSocket = new Socket(Globals.IP, Globals.PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        serverHandler = new ServerHandler(clientSocket, this, globals);
        serverHandler.start();
    }

    public void sendPackage(ChatPackage chatPackage) {
        if (out == null) return;
        out.println(chatPackage);
    }

    public void sendPackage(ChatPackage chatPackage, String message, Runnable runnable) {
        new ResponseHandler(message, runnable, globals);
        sendPackage(chatPackage);
    }

    public void sendPackage(ChatPackage chatPackage, String message, Runnable success, HashMap<Integer, Consumer<String>> fails) {
        new ResponseHandler(message, success, fails, globals);
        sendPackage(chatPackage);
    }

    @Override
    public void sendMessage(Message message) {
        globals.systemHelper.log("ServerConnection sendMessage " + message);
        if (message.getUserReceiver() != null) {
            if (ClientGlobals.security) {
                sendSecureMessage(message);

            } else {
                sendPackage(new MsgPackage(
                        message.getUserReceiver().getName(),
                        message.getText()));
            }

        } else if (message.getGroupReceiver() != null) {
            sendPackage(new GbcstPackage(
                    message.getGroupReceiver().getName(),
                    message.getText()));
        }
    }

    private void sendSecureMessage(Message message) {
        var encryptionHelper = message.getUserReceiver().getSymmetricEncryptionHelper();
        if (!encryptionHelper.isSet()) {
            encryptionHelper.createSecrets();
            sendPackage(new RqpkPackage(
                    message.getUserReceiver().getName()
            ));
        }
        try {
            sendPackage(new MsgsPackage(
                    message.getUserReceiver().getName(),
                    encryptionHelper.encrypt(message.getText())
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createGroup(String name) {
        globals.systemHelper.log("ServerConnection createGroup " + name);
        sendPackage(new CgrpPackage(name));
    }

    @Override
    public void joinedSet(Group group, boolean joined) {
        globals.systemHelper.log("ServerConnection joinedSet " + group + " " + joined);
        if (joined) {
            sendPackage(new JgrpPackage(group.getName()));
        } else {
            sendPackage(new LgrpPackage(group.getName()));
        }
    }

    @Override
    public void messageAdded(Group group, Message message) {
    }

    @Override
    public void exiting() {
        globals.systemHelper.log("ServerConnection exiting");

        if (clientSocket == null) return;

        sendPackage(new QuitPackage());

        serverHandler.interrupt();
        try {
            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void requestUpload(User user, String fileName, int fileSize, byte[] hash, byte[] connection) {
        ChatPackage pkg = new UprqPackage(user.getName(), fileName, fileSize, hash, connection);
        sendPackage(pkg);
    }

    @Override
    public void acceptDownload(User user, byte[] hash, byte[] connection) {
        sendPackage(new DnacPackage(user.getName(), hash, connection));
    }

    @Override
    public void rejectDownload(User user) {

    }

}
