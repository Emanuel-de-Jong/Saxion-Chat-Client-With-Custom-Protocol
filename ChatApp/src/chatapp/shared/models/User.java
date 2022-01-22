package chatapp.shared.models;

import chatapp.client.ClientGlobals;
import chatapp.client.SymmetricEncryptionHelper;
import chatapp.shared.Globals;
import chatapp.shared.models.chatpackages.encryption.MsgsPackage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class User {

    private final String name;
    private boolean chatAdded = false;
    private final ArrayList<Message> privateMessages = new ArrayList<>();
    protected Globals globals;
    private final boolean verified;
    private SymmetricEncryptionHelper symmetricEncryptionHelper = new SymmetricEncryptionHelper();
    private PublicKey publicKey;
    private final Queue<MsgsPackage> decryptionQueue = new LinkedList<>();


    public User(String name, boolean verified, Globals globals) {
        this.name = name;
        this.globals = globals;
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getName() {
        return name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isChatAdded() {
        return chatAdded;
    }

    public void setChatAdded(boolean chatAdded) {
        if (this.chatAdded != chatAdded) {
            this.chatAdded = chatAdded;
            globals.listeners.user.forEach(l -> l.chatAddedSet(this, chatAdded));
        }
    }

    public SymmetricEncryptionHelper getSymmetricEncryptionHelper() {
        return symmetricEncryptionHelper;
    }

    public ArrayList<Message> getPrivateMessages() {
        return privateMessages;
    }

    public void addPrivateMessage(Message message) {
        privateMessages.add(message);
        globals.listeners.user.forEach(l -> l.privateMessageAdded(this, message));
    }

    public void addToDecryptionQueue(MsgsPackage msgsPackage) {
        decryptionQueue.offer(msgsPackage);
    }

    public void decryptQueue() {
        if (!(globals instanceof ClientGlobals clientGlobals)) throw new IllegalCallerException("Cannot call decrypted without having a users list");
        if (decryptionQueue.size() > 0) setChatAdded(true);

        MsgsPackage msgsPackage;
        while ((msgsPackage = decryptionQueue.poll()) != null) {
            try {
                String messageText = symmetricEncryptionHelper.decrypt(msgsPackage.getMessage());
                addPrivateMessage(new Message(messageText,clientGlobals.users.get(msgsPackage.getSender())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        User u = (User) o;
        return name.equals(u.getName());
    }

    @Override
    public String toString() {
        //todo: make sure this works without breaking anything!!!
        //because you cannot check all usages of tostring there might still be a case where this might fail.
        return (verified ? "*" : "") +
                name;
    }

}
