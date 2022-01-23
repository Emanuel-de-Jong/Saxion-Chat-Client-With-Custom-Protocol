package chatapp.client.data;

import chatapp.client.ClientGlobals;
import chatapp.client.SymmetricEncryptionHelper;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;
import chatapp.shared.models.chatpackages.encryption.MsgsPackage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class Users extends HashMap<String, User> implements ServerConnectionListener {

    private final ClientGlobals globals;

    /**
     * hashmap of all users
     * @param globals
     */
    public Users(ClientGlobals globals) {
        this.globals = globals;
        globals.clientListeners.serverConnection.add(this);
    }

    /**
     * set chat added for all users
     */
    public void setChatAdded(boolean chatAdded) {
        for (User user : values()) {
            user.setChatAdded(chatAdded);
        }
    }

    /**
     * all users that have chatadded set to true
     * @param chatAdded
     * @return
     */
    public ArrayList<User> valuesByChatAdded(boolean chatAdded) {
        ArrayList<User> filtered = new ArrayList<>();
        for (User user : values()) {
            if (user.isChatAdded() == chatAdded) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    /**
     * add a user
     * @param user
     */
    public void add(User user) {
        put(user.getName(), user);
        globals.clientListeners.users.forEach(l -> l.userAdded(user));
    }

    /**
     * remove a user
     * @param userName
     */
    public void remove(String userName) {
        if (!this.containsKey(userName))
            return;

        User user = this.get(userName);
        this.remove(user);
        globals.clientListeners.users.forEach(l -> l.userRemoved(user));
    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        switch (chatPackage.getType()) {
            case USR -> addNewUser((UsrPackage) chatPackage);
            case USRS -> addNewUsers((UsrsPackage) chatPackage);
            case MSG -> addNewMessage((MsgPackage) chatPackage);
            case MSGS -> addNewEncryptedMessage((MsgsPackage) chatPackage);
            case DSCND -> removeUser((DscndPackage) chatPackage);
        }
    }

    /**
     * add a new user
     * @param usrPackage
     */
    private void addNewUser(UsrPackage usrPackage) {
        if (!usrPackage.getUserName().equals(globals.currentUser.getName())) {
            globals.systemHelper.log("Users addNewUser " + usrPackage);
            add(new User(usrPackage.getUserName(), usrPackage.isVerified(), globals));
        }
    }

    /**
     * add multiple new users
     * @param usrsPackage
     */
    private void addNewUsers(UsrsPackage usrsPackage) {
        globals.systemHelper.log("Users addNewUsers " + usrsPackage);
        for (String userName : usrsPackage.getUserNames()) {
            if (!userName.equals(globals.currentUser.getName())) {
                add(new User(userName, usrsPackage.isVerified(userName), globals));
            }
        }
    }

    /**
     * add a new message
     * @param msgPackage
     */
    private void addNewMessage(MsgPackage msgPackage) {
        globals.systemHelper.log("Users addNewMessage " + msgPackage);
        Message message;
        if (msgPackage.getSender().equals(globals.currentUser.getName())) { //if current user is the sender
            message = new Message(msgPackage.getMessage(), globals.currentUser);
            this.get(msgPackage.getReceiver()).addPrivateMessage(message);
        } else {
            User user = this.get(msgPackage.getSender());
            user.setChatAdded(true);
            message = new Message(msgPackage.getMessage(), user);
            user.addPrivateMessage(message);
        }
    }

    /**
     * add new encrypted message
     * @param msgsPackage
     */
    private void addNewEncryptedMessage(MsgsPackage msgsPackage) {
        globals.systemHelper.log("Users addNewMessage " + msgsPackage);
        User user = msgsPackage.getSender().equals(globals.currentUser.getName()) ?
                this.get(msgsPackage.getReceiver()) :
                this.get(msgsPackage.getSender());
        if (user == null) throw new IllegalArgumentException("User does not exist");
        SymmetricEncryptionHelper enc = user.getSymmetricEncryptionHelper();
        if (enc.isSet()) { //if able to decrypt decrypt if not put it on the queue to be decrypted
            try {
                String messageText = enc.decrypt(msgsPackage.getMessage());
                addNewMessage(new MsgPackage(msgsPackage.getSender(), msgsPackage.getReceiver(), messageText));
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException | BadPaddingException e) {
                addToDecryptionQueue(msgsPackage);
            }
        } else {
            addToDecryptionQueue(msgsPackage);
        }
    }

    //add to decryption queue
    public void addToDecryptionQueue(MsgsPackage msgsPackage) {
        if (msgsPackage.getSender().equals(globals.currentUser.getName())) {
            this.get(msgsPackage.getReceiver()).addToDecryptionQueue(msgsPackage);
        } else {
            this.get(msgsPackage.getSender()).addToDecryptionQueue(msgsPackage);
        }
    }

    /**
     * remove a user
     * @param dscndPackage
     */
    private void removeUser(DscndPackage dscndPackage) {
        globals.systemHelper.log("Users removeUser " + dscndPackage);
        remove(dscndPackage.getUserName());
    }

}
