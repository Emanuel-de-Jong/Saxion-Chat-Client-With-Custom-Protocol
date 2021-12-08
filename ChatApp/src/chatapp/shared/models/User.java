package chatapp.shared.models;

import chatapp.shared.interfaces.UserListener;

import java.util.ArrayList;

public class User {

    public static ArrayList<UserListener> listeners = new ArrayList<>();

    private final String name;
    private boolean chatAdded = false;
    private final ArrayList<Message> privateMessages = new ArrayList<>();


    public User(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public boolean isChatAdded() {
        return chatAdded;
    }

    public void setChatAdded(boolean chatAdded) {
        this.chatAdded = chatAdded;
        listeners.forEach(l -> l.chatAddedSet(this, chatAdded));
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
