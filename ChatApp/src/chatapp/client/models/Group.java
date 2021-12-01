package chatapp.client.models;

import java.util.ArrayList;

public class Group {

    private String name;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Message> messages = new ArrayList<>();


    public Group(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }


    @Override
    public String toString() {
        return name;
    }

}
