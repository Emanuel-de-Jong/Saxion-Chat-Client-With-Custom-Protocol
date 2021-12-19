package chatapp.client.data;

import chatapp.client.ClientGlobals;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

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
            case DSCND -> removeUser((DscndPackage) chatPackage);
        }
    }

    private void addNewUser(UsrPackage usrPackage) {
        System.out.println("Users chatPackageReceived " + usrPackage);
        if (!usrPackage.getUserName().equals(globals.currentUser.getName())) {
            if (!usrPackage.getUserName().equals(globals.currentUser.getName())) {
                add(new User(usrPackage.getUserName(), usrPackage.isVerified(), globals));
            }
        }
    }

    private void addNewUsers(UsrsPackage usrsPackage) {
        System.out.println("Users chatPackageReceived " + usrsPackage);
        for (String userName : usrsPackage.getUserNames()) {
            if (!userName.equals(globals.currentUser.getName())) {
                add(new User(userName, usrsPackage.isVerified(userName), globals));
            }
        }
    }

    private void addNewMessage(MsgPackage msgPackage) {
        System.out.println("Users chatPackageReceived " + msgPackage);

        Message message;
        if (msgPackage.getSender().equals(globals.currentUser.getName())) {
            message = new Message(msgPackage.getMessage(), globals.currentUser);
            this.get(msgPackage.getReceiver()).addPrivateMessage(message);
        } else {
            User user = this.get(msgPackage.getSender());
            message = new Message(msgPackage.getMessage(), user);
            user.addPrivateMessage(message);
        }
    }

    private void removeUser(DscndPackage dscndPackage) {
        remove(dscndPackage.getUserName());
    }

}
