package chatapp.client;

import chatapp.client.filetransfer.DownloadHandler;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.PongPackage;
import chatapp.shared.models.chatpackages.encryption.RqpkPackage;
import chatapp.shared.models.chatpackages.encryption.SeskPackage;
import chatapp.shared.models.chatpackages.filetransfer.DnrqPackage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ServerPackageHandler implements ServerConnectionListener {
    private ServerConnection serverConnection;
    private ClientGlobals globals;

    public ServerPackageHandler(ServerConnection serverConnection, ClientGlobals globals) {
        this.serverConnection = serverConnection;
        this.globals = globals;
        globals.clientListeners.serverConnection.add(this);
    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        switch (chatPackage.getType()) {
            case PING -> serverConnection.sendPackage(new PongPackage());
            case DNRQ -> dnrq((DnrqPackage) chatPackage);
            case RQPK -> rqpk((RqpkPackage) chatPackage);
            case SESK -> sesk((SeskPackage) chatPackage);
            case DSCN -> globals.systemHelper.restart();
        }
    }


    public void dnrq(DnrqPackage dnrqPackage) {
        new DownloadHandler(
                globals.users.get(dnrqPackage.getUser()),
                dnrqPackage.getFileName(),
                dnrqPackage.getFileSize(),
                dnrqPackage.getHash(),
                globals
        ).start();
    }

    public void rqpk(RqpkPackage rqpkPackage) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (rqpkPackage.isSet()) {
            User user = globals.users.get(rqpkPackage.getUser());
            if (user == null) return;
            user.setPublicKey(globals.asymmetricEncryptionHelper.convertByteArrayIntoPublicKey(rqpkPackage.getKey()));
            sendSessionKey(user);
        } else {
            rqpkPackage.setKey(globals.asymmetricEncryptionHelper.getPublicKey().getEncoded());
            serverConnection.sendPackage(rqpkPackage);
        }
    }

    private void sendSessionKey(User user) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        SymmetricEncryptionHelper enc = user.getSymmetricEncryptionHelper();
        if (!enc.isSet()) enc.createSecrets();
        byte[] key = enc.getSecretKey().getEncoded();
        byte[] iv = enc.getInitializationVector();
        if (iv == null || key == null) throw new IllegalStateException("For some reason the create new key didn't work");
        key = globals.asymmetricEncryptionHelper.encrypt(key, user.getPublicKey());
        iv = globals.asymmetricEncryptionHelper.encrypt(iv, user.getPublicKey());
        serverConnection.sendPackage(new SeskPackage(user.getName(),key,iv));

    }

    public void sesk(SeskPackage seskPackage) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        User user = globals.users.get(seskPackage.getUser());
        if (seskPackage.isSet()) {
            byte[] key = globals.asymmetricEncryptionHelper.decrypt(seskPackage.getKey());
            byte[] iv = globals.asymmetricEncryptionHelper.decrypt(seskPackage.getInitializationVector());
            user.getSymmetricEncryptionHelper().setSecrets(key,iv);
            user.decryptQueue();
        } else {
            if (user.getPublicKey() != null) {
                sendSessionKey(user);
            } else {
                serverConnection.sendPackage(new RqpkPackage(user.getName()));
            }
        }
    }
}
