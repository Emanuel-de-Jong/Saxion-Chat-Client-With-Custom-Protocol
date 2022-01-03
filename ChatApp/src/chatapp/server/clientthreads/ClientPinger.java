package chatapp.server.clientthreads;

import chatapp.server.ServerGlobals;
import chatapp.shared.models.chatpackages.DscnPackage;
import chatapp.shared.models.chatpackages.PingPackage;

public class ClientPinger extends Thread {

    private static final long MILLIS_PER_PING = 10 * 1_000L;
    private static final long MILLIS_FOR_PONG = 3 * 1_000L;

    private final ClientHandler clientHandler;
    private long lastPingTime;
    private long lastPongTime;


    public ClientPinger(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void run() {
        lastPingTime = System.currentTimeMillis();
        lastPongTime = System.currentTimeMillis();

        try {
            while (ServerGlobals.PING && !Thread.currentThread().isInterrupted()) {
                Thread.sleep(MILLIS_PER_PING);

                if (!pongReceivedInTime()) {
                    clientHandler.sendPackage(new DscnPackage("Pong timeout"));
                    clientHandler.interrupt();
                    return;
                }

                clientHandler.sendPackage(new PingPackage());
                lastPingTime = System.currentTimeMillis();
            }
        } catch (InterruptedException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public synchronized void setLastPongTime(long lastPongTime) {
        this.lastPongTime = lastPongTime;
    }

    private synchronized boolean pongReceivedInTime() {
        return (lastPingTime - lastPongTime) <= MILLIS_FOR_PONG;
    }

}
