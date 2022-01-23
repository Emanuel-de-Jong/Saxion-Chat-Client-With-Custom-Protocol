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


    private final Socket clientSocket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final ServerHandler serverHandler;
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

    /**
     * send a package to the server
     * @param chatPackage
     */
    public void sendPackage(ChatPackage chatPackage) {
        if (out == null) return;
        out.println(chatPackage);
    }

    /**
     * send a package and do something on an ok response
     * @param chatPackage
     * @param message
     * @param runnable
     */
    public void sendPackage(ChatPackage chatPackage, String message, Runnable runnable) {
        new ResponseHandler(message, runnable, globals);
        sendPackage(chatPackage);
    }

    /**
     * send a package and do something on an ok response and set errors
     * @param chatPackage
     * @param message
     * @param success
     * @param fails
     */
    public void sendPackage(ChatPackage chatPackage, String message, Runnable success, HashMap<Integer, Consumer<String>> fails) {
        new ResponseHandler(message, success, fails, globals);
        sendPackage(chatPackage);
    }

    /**
     * send a message
     * @param message
     */
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

    /**
     * send an encrypted message
     * @param message
     */
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

    /**
     * create a new group
     * @param name
     */
    @Override
    public void createGroup(String name) {
        globals.systemHelper.log("ServerConnection createGroup " + name);
        sendPackage(new CgrpPackage(name));
    }

    /**
     * set joined status of a group.
     * @param group
     * @param joined
     */
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

    /**
     * exit
     */
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

    /**
     * request to upload a file
     * @param user
     * @param fileName
     * @param fileSize
     * @param hash
     * @param connection
     */
    @Override
    public void requestUpload(User user, String fileName, int fileSize, byte[] hash, byte[] connection) {
        ChatPackage pkg = new UprqPackage(user.getName(), fileName, fileSize, hash, connection);
        sendPackage(pkg);
    }

    /**
     * accept to download a file
     * @param user
     * @param hash
     * @param connection
     */
    @Override
    public void acceptDownload(User user, byte[] hash, byte[] connection) {
        sendPackage(new DnacPackage(user.getName(), hash, connection));
    }

    @Override
    public void rejectDownload(User user) {

    }

}
