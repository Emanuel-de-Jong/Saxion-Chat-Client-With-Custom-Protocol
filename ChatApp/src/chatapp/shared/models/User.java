package chatapp.shared.models;

import chatapp.shared.Globals;

import java.util.ArrayList;

public class User {

    private final String name;
    private boolean chatAdded = false;
    private final ArrayList<Message> privateMessages = new ArrayList<>();
    protected Globals globals;
    private boolean verified;


    public User(String name, boolean verified, Globals globals) {
        this.name = name;
        this.globals = globals;
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
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

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        User u = (User) o;
        return name.equals(u.getName());
    }

    @Override
    public String toString() {
        //todo: make sure this works without breaking anything!!!
        //because you cannot check all usages of tostring there might still be a case where this might fail.
        return (verified ? "*" : "") +
                name;
    }

}
