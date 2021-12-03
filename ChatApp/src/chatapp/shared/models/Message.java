package chatapp.shared.models;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {

    private final String text;
    private final LocalTime time;
    private final User sender;
    private User userReceiver;
    private Group groupReceiver;

    public Message(String text, User sender) {
        this.text = text;
        this.sender = sender;

        time = LocalTime.now();
    }

    public Message(String text, User sender, User userReceiver) {
        this(text, sender);
        this.userReceiver = userReceiver;
    }

    public Message(String text, User sender, Group groupReceiver) {
        this(text, sender);
        this.groupReceiver = groupReceiver;
    }


    public String getText() {
        return text;
    }

    public User getUserReceiver() {
        return userReceiver;
    }

    public Group getGroupReceiver() {
        return groupReceiver;
    }


    @Override
    public String toString() {
        return String.format("%s %s: %s",
                time.format(DateTimeFormatter.ofPattern("HH:mm")),
                sender.getName(),
                text);
    }

}
