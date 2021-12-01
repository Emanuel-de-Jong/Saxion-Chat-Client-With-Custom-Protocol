package chatapp.client.models;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class Message {

    private String text;
    private LocalTime time;
    private User user;

    public Message(String text, User user) {
        this.text = text;
        this.user = user;

        time = LocalTime.now();
    }


    public String getText() {
        return text;
    }

    public LocalTime getTime() {
        return time;
    }

    public User getUser() {
        return user;
    }


    @Override
    public String toString() {
        return String.format("%s %s: %s",
                time.format(DateTimeFormatter.ofPattern("HH:mm")),
                user.getName(),
                text);
    }

}
