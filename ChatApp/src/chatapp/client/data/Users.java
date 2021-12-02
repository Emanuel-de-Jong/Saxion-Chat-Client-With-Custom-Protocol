package chatapp.client.data;

import chatapp.client.Config;
import chatapp.client.ServerConnection;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.client.interfaces.UsersListener;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Users implements ServerConnectionListener {

    public static ArrayList<UsersListener> listeners = new ArrayList<>();

    public static Users instance = new Users();

    public HashMap<String, User> users = new HashMap<>();


    public Users() {
        ServerConnection.listeners.add(this);
    }


    public HashMap<String, User> getUsers() {
        return users;
    }

    public User getUser(String userName) {
        return users.get(userName);
    }

    public void addUser(User user) {
        users.put(user.getName(), user);
        listeners.forEach(l -> l.userAdded(user));
    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        if (chatPackage.getType() == ChatPackageType.USR) {
            UsrPackage usrPackage = (UsrPackage) chatPackage;
            System.out.println("Users chatPackageReceived " + usrPackage);
            if (!usrPackage.getUserName().equals(Config.currentUser.getName()))
                addUser(new User(usrPackage.getUserName()));
        }
        else if (chatPackage.getType() == ChatPackageType.USRS) {
            UsrsPackage usrsPackage = (UsrsPackage) chatPackage;
            System.out.println("Users chatPackageReceived " + usrsPackage);
            for (String userName : usrsPackage.getUserNames()) {
                if (!userName.equals(Config.currentUser.getName()))
                    addUser(new User(userName));
            }
        }
        else if (chatPackage.getType() == ChatPackageType.MSG) {
            MsgPackage msgPackage = (MsgPackage) chatPackage;
            System.out.println("Users chatPackageReceived " + msgPackage);
            User user = users.get(msgPackage.getSender());
            user.addPrivateMessage(new Message(msgPackage.getMessage(), user));
        }
    }

}
