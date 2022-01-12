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
    private String fileName;
    private int fileSize;
    private byte[] hash;
    private byte[] connection;
    private User targetUser;

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

        run();
    }

    public void run() throws IOException {
        socket = new Socket(Globals.IP, Globals.PORT + 1);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                System.out.println("READING BYTES");
                connection = in.readNBytes(8);
                requestUpload();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        globals.clientListeners.serverConnection.add(this);
    }

    private void requestUpload() {
        System.out.println("requestUpload");
        globals.clientListeners.uploads.forEach(uploadListener -> uploadListener.requestUpload(targetUser, fileName, fileSize, hash, connection));
    }


    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        switch (chatPackage.getType()) {

        }
    }
}
