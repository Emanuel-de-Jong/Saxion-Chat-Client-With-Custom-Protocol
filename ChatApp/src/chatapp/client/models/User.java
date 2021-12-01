package chatapp.client.models;

import java.util.ArrayList;

public class User {

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
        privateMessages.add(message);
    }


    @Override
    public String toString() {
        return name;
    }

}
