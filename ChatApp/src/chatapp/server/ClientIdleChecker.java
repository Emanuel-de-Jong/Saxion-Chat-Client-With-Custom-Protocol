package chatapp.server;

import chatapp.server.models.Client;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class ClientIdleChecker extends Thread {

    private Client client;
    private ServerGlobals globals;

    private HashMap<String, Long> groupMsgTimes = new HashMap<>();

    public ClientIdleChecker(Client client, ServerGlobals globals) {
        this.globals = globals;
        this.client = client;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(30 * 1_000L);

                Long currentTime = System.currentTimeMillis();
                for (Entry<String, Long> entry : groupMsgTimes.entrySet().stream().toList()) {
                    if (currentTime - entry.getValue() > 120 * 1_000L) {
                        client.getPackageHandler().sendPackage(new GtmtPackage(entry.getKey()));
                    }
                }
            } catch (InterruptedException ex) {
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addGroup(String groupName) {
        if (!groupName.equals(Globals.publicGroupName)) {
            groupMsgTimes.put(groupName, System.currentTimeMillis());
        }
    }

    public void updateGroup(String groupName) {
        addGroup(groupName);
    }

    public void removeGroup(String groupName) {
        if (!groupName.equals(Globals.publicGroupName)) {
            groupMsgTimes.remove(groupName);
        }
    }

}
