package chatapp.client.filetransfer;

import chatapp.shared.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UploadHandler {
    private byte[] file;
    private byte[] hash;
    private User user;

    public UploadHandler(byte[] file, User user, String algorithm) throws NoSuchAlgorithmException {
        this.file = file;
        this.user = user;
        final MessageDigest md = MessageDigest.getInstance(algorithm);
        this.hash = md.digest(file);
    }
}
