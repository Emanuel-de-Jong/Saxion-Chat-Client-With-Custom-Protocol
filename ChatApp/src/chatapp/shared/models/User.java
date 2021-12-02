package chatapp.shared.models;

import chatapp.client.interfaces.UserListener;

import java.util.ArrayList;

public class User {

    public static ArrayList<UserListener> listeners = new ArrayList<>();

    private String name;
    private ArrayList<Message> privateMessages = new ArrayList<>();


    public User(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public ArrayList<Message> getPrivateMessages() {
        return privateMessages;
    }

    public void addPrivateMessage(Message message) {
        listeners.forEach(l -> l.privateMessageAdded(this, message));
        privateMessages.add(message);
    }


    @Override
    public String toString() {
        return name;
    }

}
