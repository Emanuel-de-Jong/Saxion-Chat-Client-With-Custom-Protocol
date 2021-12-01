package chatapp.client.data;

import chatapp.client.interfaces.UsersListener;
import chatapp.client.models.Group;
import chatapp.client.models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Users {

    public static ArrayList<UsersListener> listeners = new ArrayList<>();

    public static HashMap<String, User> users = new HashMap<>();


    public static HashMap<String, User> getUsers() {
        return users;
    }

    public static void addUser(User user) {
        users.put(user.getName(), user);
        listeners.forEach(l -> l.userAdded(user));
    }

}
