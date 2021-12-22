package chatapp.server.ClientThreads;

import chatapp.shared.Globals;
import chatapp.shared.models.chatpackages.GtmtPackage;

import java.util.HashMap;
import java.util.Map;

public class ClientIdleChecker extends Thread {

    private static final long MILLIS_PER_CHECK = 30 * 1_000L;
    private static final long MILLIS_BEFORE_TIMEOUT = 120 * 1_000L;

    private final ClientHandler clientHandler;
    private final HashMap<String, Long> groupMsgTimes = new HashMap<>();

    public ClientIdleChecker(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(MILLIS_PER_CHECK);

                Long currentTime = System.currentTimeMillis();
                for (Map.Entry<String, Long> entry : groupMsgTimes.entrySet().stream().toList()) {
                    if ((currentTime - entry.getValue()) > MILLIS_BEFORE_TIMEOUT) {
                        clientHandler.sendPackage(new GtmtPackage(entry.getKey()));
                    }
                }
            }
        } catch (InterruptedException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
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
