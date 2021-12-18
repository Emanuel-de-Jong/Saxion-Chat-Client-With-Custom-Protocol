package chatapp.client;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class SystemHelper {

    public static void exit() {
        System.exit(0);
    }

    public static void restart() {
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
