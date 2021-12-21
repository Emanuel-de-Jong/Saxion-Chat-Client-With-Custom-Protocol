package chatapp.server;

import chatapp.server.models.Client;
import chatapp.shared.models.chatpackages.DscnPackage;
import chatapp.shared.models.chatpackages.PingPackage;

import java.io.IOException;
import java.io.PrintWriter;

public class ClientPinger extends Thread {

    private ClientHandler clientHandler;
    private final ServerGlobals globals;
    private long lastPongTime;


    public ClientPinger(ClientHandler clientHandler, ServerGlobals globals) {
        this.clientHandler = clientHandler;
        this.globals = globals;
    }

    public void run() {
        lastPongTime = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(ServerGlobals.secondsPerPing * 1_000L);

                if (!pongReceivedInTime()) {
                    clientHandler.sendPackage(new DscnPackage());
                }

                clientHandler.sendPackage(new PingPackage());
            } catch (InterruptedException ex) {
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public synchronized void setLastPongTime(long lastPongTime) {
        this.lastPongTime = lastPongTime;
    }

    private synchronized boolean pongReceivedInTime() {
        return (System.currentTimeMillis() - lastPongTime) <= (ServerGlobals.secondsForPong * 1_000L);
    }

}
