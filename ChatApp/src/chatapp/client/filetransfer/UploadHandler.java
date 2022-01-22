package chatapp.client.filetransfer;

import chatapp.client.ClientGlobals;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UploadHandler implements ServerConnectionListener {
    private final ClientGlobals globals;

    private final byte[] file;
    private final String fileName;
    private final int fileSize;
    private final byte[] hash;
    private byte[] connection;
    private final User targetUser;

    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;

    public UploadHandler(byte[] file, String fileName, User targetUser, ClientGlobals globals, String algorithm) throws NoSuchAlgorithmException, IOException {
        this.file = file;
        this.fileName = fileName;
        this.fileSize = file.length;
        this.targetUser = targetUser;
        this.globals = globals;

        final MessageDigest md = MessageDigest.getInstance(algorithm);
        this.hash = md.digest(file);

        closeAfterTimeout(5 * 60 * 1000);
        run();
    }


    public void run() throws IOException {
        socket = new Socket(Globals.IP, Globals.PORT + 1);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                connection = in.readNBytes(8);
                requestUpload();
            } catch (IOException e) {
                close();
                e.printStackTrace();
            }
        }).start();
        globals.clientListeners.serverConnection.add(this);
    }

    private void requestUpload() {
        targetUser.addPrivateMessage(new Message(globals.currentUser + " requests to send file: " + fileName + " (" + fileSize + " bytes).", null));
        globals.clientListeners.uploads.forEach(uploadListener -> uploadListener.requestUpload(targetUser, fileName, fileSize, hash, connection));
    }


    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        try {
            if (chatPackage.getType() == ChatPackageType.UPAC) {
                targetUser.addPrivateMessage(new Message(globals.currentUser + ": started uploading " + fileName + ".", null));
                out.write(file);
                targetUser.addPrivateMessage(new Message(globals.currentUser + ": finished uploading " + fileName + ".", null));
                socket.close();
            }
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
    }

    private void closeAfterTimeout(int timeout) {
        new Thread(() -> {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            close();
        }).start();
    }

    private void close() {
        try {
            targetUser.addPrivateMessage(new Message(globals.currentUser + " failed to send file: " + fileName, null));
            globals.clientListeners.serverConnection.remove(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
