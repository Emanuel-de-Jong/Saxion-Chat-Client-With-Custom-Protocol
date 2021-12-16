package chatapp.server;

import chatapp.server.models.Client;
import chatapp.shared.models.chatpackages.PingPackage;

import java.io.IOException;
import java.io.PrintWriter;

public class ClientPinger extends Thread {

    private Client client;
    private ServerGlobals globals;
    private long timeSincePong = 0;


    public ClientPinger(Client client, ServerGlobals globals) {
        this.globals = globals;
        this.client = client;
        System.nanoTime();
        client.setPinger(this);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(2000);

                if (!pongReceivedInTime()) {
                    System.out.println(client.getName() + " NO PONG SHOULD BE DISCONNECTED");
                }

                sendPing();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public synchronized void setTimeSincePong(long timeSincePong) {
        this.timeSincePong = timeSincePong;
    }


    private void sendPing() throws IOException {
        PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
        out.println(new PingPackage());
    }

    private synchronized boolean pongReceivedInTime() {
        return (System.currentTimeMillis() - timeSincePong) <= (10 * 1_000);
    }

}
