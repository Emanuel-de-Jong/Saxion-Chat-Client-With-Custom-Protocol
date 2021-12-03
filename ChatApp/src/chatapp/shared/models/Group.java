package chatapp.shared.models;

import chatapp.shared.interfaces.GroupListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {

    public static ArrayList<GroupListener> listeners = new ArrayList<>();

    private final String name;
    private boolean joined = false;
    private final HashMap<String, User> users = new HashMap<>();
    private final ArrayList<Message> messages = new ArrayList<>();


    public Group(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.put(user.getName(), user);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        listeners.forEach(l -> l.messageAdded(this, message));
        messages.add(message);
    }


    @Override
    public String toString() {
        return name;
    }

}
