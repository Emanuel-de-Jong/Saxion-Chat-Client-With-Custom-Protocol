package chatapp.client.models;

import chatapp.client.enums.LogLevel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {

    private final String text;
    private final String source;
    private final LogLevel level;
    private final LocalTime time;


    public Log(String text, String source, LogLevel level) {
        this.text = text;
        this.source = source;
        this.level = level;

        time = LocalTime.now();
    }


    public LogLevel getLevel() {
        return level;
    }


    @Override
    public String toString() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm")) + " " +
                source + ": " +
                text;
    }

}
