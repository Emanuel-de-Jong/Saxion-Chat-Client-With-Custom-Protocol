package chatapp.server;

import chatapp.server.models.Client;
import chatapp.shared.models.chatpackages.DscnPackage;
import chatapp.shared.models.chatpackages.PingPackage;

import java.io.IOException;
import java.io.PrintWriter;

public class ClientPinger extends Thread {

    private final Client client;
    private final ServerGlobals globals;
    private long lastPongTime;


    public ClientPinger(Client client, ServerGlobals globals) {
        this.globals = globals;
        this.client = client;
    }

    public void run() {
        lastPongTime = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(ServerGlobals.secondsPerPing * 1_000L);

                if (!pongReceivedInTime()) {
                    client.getPackageHandler().sendPackage(new DscnPackage());
                }

                sendPing();
            } catch (InterruptedException ex) {
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public synchronized void setLastPongTime(long lastPongTime) {
        this.lastPongTime = lastPongTime;
    }


    private void sendPing() throws IOException {
        PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
        out.println(new PingPackage());
    }

    private synchronized boolean pongReceivedInTime() {
        return (System.currentTimeMillis() - lastPongTime) <= (ServerGlobals.secondsForPong * 1_000L);
    }

}
