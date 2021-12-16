package chatapp.shared.models;

import chatapp.shared.Globals;
import chatapp.shared.interfaces.UserListener;

import java.util.ArrayList;

public class User {

    private final String name;
    private boolean chatAdded = false;
    private final ArrayList<Message> privateMessages = new ArrayList<>();
    protected Globals globals;


    public User(String name, Globals globals) {
        this.name = name;
        this.globals = globals;
    }


    public String getName() {
        return name;
    }

    public boolean isChatAdded() {
        return chatAdded;
    }

    public void setChatAdded(boolean chatAdded) {
        if (this.chatAdded != chatAdded) {
            this.chatAdded = chatAdded;
            globals.listeners.user.forEach(l -> l.chatAddedSet(this, chatAdded));
        }
    }

    public ArrayList<Message> getPrivateMessages() {
        return privateMessages;
    }

    public void addPrivateMessage(Message message) {
        globals.listeners.user.forEach(l -> l.privateMessageAdded(this, message));
        privateMessages.add(message);
    }

    public void setGlobals(Globals globals) {
        this.globals = globals;
    }

    @Override
    public String toString() {
        return name;
    }

}
