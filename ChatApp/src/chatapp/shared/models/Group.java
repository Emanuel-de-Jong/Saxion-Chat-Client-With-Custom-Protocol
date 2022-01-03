package chatapp.shared.models;

import chatapp.shared.Globals;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {

    private final String name;
    private boolean joined = false;
    private final HashMap<String, User> users = new HashMap<>();
    private final ArrayList<Message> messages = new ArrayList<>();
    private final Globals globals;


    public Group(String name, Globals globals) {
        this.name = name;
        this.globals = globals;
    }


    public String getName() {
        return name;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        if (this.joined != joined) {
            this.joined = joined;
            globals.listeners.group.forEach(l -> l.joinedSet(this, joined));
        }
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.put(user.getName(), user);
    }

    public void removeUser(User user) {
        users.remove(user.getName());
    }

    public void removeUser(String userName) {
        users.remove(userName);
    }

    public boolean hasUser(String userName) {
        return users.containsKey(userName);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
        globals.listeners.group.forEach(l -> l.messageAdded(this, message));
    }


    @Override
    public String toString() {
        return name;
    }

}
