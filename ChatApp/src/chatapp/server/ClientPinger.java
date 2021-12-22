package chatapp.server;

import chatapp.shared.models.chatpackages.DscnPackage;
import chatapp.shared.models.chatpackages.PingPackage;

public class ClientPinger extends Thread {

    private static final long MILLIS_PER_PING = 3 * 1_000L;
    private static final long MILLIS_FOR_PONG = 10 * 1_000L;

    private ClientHandler clientHandler;
    private final ServerGlobals globals;
    private long lastPongTime;


    public ClientPinger(ClientHandler clientHandler, ServerGlobals globals) {
        this.clientHandler = clientHandler;
        this.globals = globals;
    }

    public void run() {
        lastPongTime = System.currentTimeMillis();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(MILLIS_PER_PING);

                if (!pongReceivedInTime()) {
                    clientHandler.sendPackage(new DscnPackage("Pong timeout"));
                    clientHandler.interrupt();
                    return;
                }

                clientHandler.sendPackage(new PingPackage());
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
        return (System.currentTimeMillis() - lastPongTime) <= MILLIS_FOR_PONG;
    }

}
