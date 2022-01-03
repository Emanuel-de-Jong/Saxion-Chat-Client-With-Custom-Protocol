package chatapp.shared;

import chatapp.client.ClientApp;
import chatapp.client.ClientGlobals;
import chatapp.server.ServerGlobals;
import chatapp.shared.models.User;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SystemHelper {

    private static int instanceCount = 0;

    private final Globals globals;
    private final boolean isServer;
    private final int colorCode;


    public SystemHelper(Globals globals) {
        instanceCount++;

        this.globals = globals;

        isServer = globals instanceof ServerGlobals;
        colorCode = (instanceCount % 8) + 30;
    }


    public void log(String text) {
        log(text, false);
    }
    public void log(String text, boolean isProtocol) {
        if (isProtocol) text = "\u001B[33m" + text + "\u001B[0m";

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        String source;
        if (isServer) {
            source = "S";
        } else {
            User user = ((ClientGlobals) globals).currentUser;
            source = user != null ? user.getName() : "C";
        }

        int spacesBeforeText = source.length() < 8 ? 8 - source.length() : 1;
        System.out.println(String.format(
                "%s%s %s:%s%s%s",
                "\u001B[" + colorCode + "m",
                time,
                source,
                "\u001B[0m",
                " ".repeat(spacesBeforeText),
                text));
    }

    public void exit() {
        globals.listeners.systemHelper.forEach(l -> l.exiting());
        System.exit(0);
    }

    public void restart() {
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath() + " ");
        cmd.append(ClientApp.class.getName());

        try {
            Runtime.getRuntime().exec(cmd.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        exit();
    }

}
