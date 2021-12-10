package chatapp.client.data;

import chatapp.client.ClientGlobals;
import chatapp.client.ServerConnection;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.client.interfaces.UsersListener;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.MsgPackage;
import chatapp.shared.models.chatpackages.UsrPackage;
import chatapp.shared.models.chatpackages.UsrsPackage;

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

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        switch (chatPackage.getType()) {
            case USR -> addNewUser((UsrPackage) chatPackage);
            case USRS -> addNewUsers((UsrsPackage) chatPackage);
            case MSG -> addNewMessage((MsgPackage) chatPackage);
        }
    }

    public void addNewUser(UsrPackage usrPackage) {
        System.out.println("Users chatPackageReceived " + usrPackage);
        if (!usrPackage.getUserName().equals(globals.currentUser.getName()))
            add(new User(usrPackage.getUserName(), globals));

    }

    public void addNewUsers(UsrsPackage usrsPackage) {
        System.out.println("Users chatPackageReceived " + usrsPackage);
        for (String userName : usrsPackage.getUserNames()) {
            if (!userName.equals(globals.currentUser.getName()))
                add(new User(userName, globals));
        }
    }

    public void addNewMessage(MsgPackage msgPackage) {
        System.out.println("Users chatPackageReceived " + msgPackage);
        if (msgPackage.getSender().equals(globals.currentUser.getName())) {
            this.get(msgPackage.getReceiver()).addPrivateMessage(
                    new Message(msgPackage.getMessage(), globals.currentUser));
        } else {
            User user = this.get(msgPackage.getSender());
            user.addPrivateMessage(new Message(msgPackage.getMessage(), user));
        }
    }
}
