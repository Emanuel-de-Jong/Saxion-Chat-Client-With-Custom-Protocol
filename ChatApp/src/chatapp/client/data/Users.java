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


    public Users(ClientGlobals globals) {
        this.globals = globals;
        globals.clientListeners.serverConnection.add(this);
    }


    public void setChatAdded(boolean chatAdded) {
        for (User user : values()) {
            user.setChatAdded(chatAdded);
        }
    }

    public ArrayList<User> valuesByChatAdded(boolean chatAdded) {
        ArrayList<User> filtered = new ArrayList<>();
        for (User user : values()) {
            if (user.isChatAdded() == chatAdded) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    public void add(User user) {
        put(user.getName(), user);
        globals.clientListeners.users.forEach(l -> l.userAdded(user));
    }

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


    private void addNewUser(UsrPackage usrPackage) {
        if (!usrPackage.getUserName().equals(globals.currentUser.getName())) {
            globals.systemHelper.log("Users addNewUser " + usrPackage);
            add(new User(usrPackage.getUserName(), usrPackage.isVerified(), globals));
        }
    }

    private void addNewUsers(UsrsPackage usrsPackage) {
        globals.systemHelper.log("Users addNewUsers " + usrsPackage);
        for (String userName : usrsPackage.getUserNames()) {
            if (!userName.equals(globals.currentUser.getName())) {
                add(new User(userName, usrsPackage.isVerified(userName), globals));
            }
        }
    }

    private void addNewMessage(MsgPackage msgPackage) {
        globals.systemHelper.log("Users addNewMessage " + msgPackage);
        Message message;
        if (msgPackage.getSender().equals(globals.currentUser.getName())) {
            message = new Message(msgPackage.getMessage(), globals.currentUser);
            this.get(msgPackage.getReceiver()).addPrivateMessage(message);
        } else {
            User user = this.get(msgPackage.getSender());
            user.setChatAdded(true);
            message = new Message(msgPackage.getMessage(), user);
            user.addPrivateMessage(message);
        }
    }

    private void addNewEncryptedMessage(MsgsPackage msgsPackage) {
        globals.systemHelper.log("Users addNewMessage " + msgsPackage);
        User user = msgsPackage.getSender().equals(globals.currentUser.getName()) ?
                this.get(msgsPackage.getReceiver()) :
                this.get(msgsPackage.getSender());
        if (user == null) throw new IllegalArgumentException("User does not exist");
        SymmetricEncryptionHelper enc = user.getSymmetricEncryptionHelper();
        if (enc.isSet()) {
            try {
                String messageText = enc.decrypt(msgsPackage.getMessage());
                addNewMessage(new MsgPackage(msgsPackage.getSender(), msgsPackage.getReceiver(), messageText));
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                addToDecryptionQueue(msgsPackage);
            }
        } else {
            addToDecryptionQueue(msgsPackage);
        }
    }

    public void addToDecryptionQueue(MsgsPackage msgsPackage) {
        if (msgsPackage.getSender().equals(globals.currentUser.getName())) {
            this.get(msgsPackage.getReceiver()).addToDecryptionQueue(msgsPackage);
        } else {
            this.get(msgsPackage.getSender()).addToDecryptionQueue(msgsPackage);
        }
    }

    private void removeUser(DscndPackage dscndPackage) {
        globals.systemHelper.log("Users removeUser " + dscndPackage);
        remove(dscndPackage.getUserName());
    }

}
