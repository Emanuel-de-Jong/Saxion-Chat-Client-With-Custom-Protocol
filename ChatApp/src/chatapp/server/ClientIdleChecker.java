package chatapp.server;

import chatapp.server.models.Client;
import chatapp.shared.Globals;
import chatapp.shared.models.chatpackages.*;

import java.util.HashMap;
import java.util.Map.Entry;

public class ClientIdleChecker extends Thread {

    private ClientHandler clientHandler;
    private ServerGlobals globals;
    private HashMap<String, Long> groupMsgTimes = new HashMap<>();

    public ClientIdleChecker(ClientHandler clientHandler, ServerGlobals globals) {
        this.clientHandler = clientHandler;
        this.globals = globals;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(30 * 1_000L);

                Long currentTime = System.currentTimeMillis();
                for (Entry<String, Long> entry : groupMsgTimes.entrySet().stream().toList()) {
                    if (currentTime - entry.getValue() > 120 * 1_000L) {
                        clientHandler.sendPackage(new GtmtPackage(entry.getKey()));
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
