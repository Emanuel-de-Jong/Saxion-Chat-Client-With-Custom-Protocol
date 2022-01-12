package chatapp.client.filetransfer;

import chatapp.client.ClientGlobals;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.Globals;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UploadHandler implements ServerConnectionListener {
    private ClientGlobals globals;

    private byte[] file;
    private byte[] hash;
    private byte[] connection;
    private User user;

    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;

    public UploadHandler(byte[] file, User user, ClientGlobals globals, String algorithm) throws NoSuchAlgorithmException, IOException {
        this.file = file;
        this.user = user;
        final MessageDigest md = MessageDigest.getInstance(algorithm);
        this.hash = md.digest(file);
        socket = new Socket(Globals.IP, Globals.PORT + 1);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                connection = in.readNBytes(8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        globals.clientListeners.serverConnection.add(this);
    }

    private void requestUpload() {
        globals.clientListeners.uploads.forEach(uploadListener -> uploadListener.requestUpload(connection, hash, user));
    }


    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        switch (chatPackage.getType()) {

        }
    }
}
