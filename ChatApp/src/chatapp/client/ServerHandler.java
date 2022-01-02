package chatapp.client;

import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.GbcstPackage;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.PongPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ConcurrentModificationException;

public class ServerHandler extends Thread {

    private final Socket clientSocket;
    private final ServerConnection serverConnection;
    private PrintWriter out;
    private BufferedReader in;
    private final ClientGlobals globals;

    public ServerHandler(Socket clientSocket, ServerConnection serverConnection, ClientGlobals globals) {
        this.clientSocket = clientSocket;
        this.serverConnection = serverConnection;
        this.globals = globals;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String packageStr;
            while (!Thread.currentThread().isInterrupted() &&
                    !(packageStr = in.readLine()).equals("false")) {
                ChatPackage chatPackage = ChatPackageHelper.deserialize(packageStr, true);
                System.out.println("CP: " + chatPackage);

                switch (chatPackage.getType()) {
                    case PING:
                        serverConnection.sendPackage(new PongPackage());
                        break;
                    case DSCN:
                        globals.systemHelper.restart();
                    default:
                        for (ServerConnectionListener serverConnectionListener : globals.clientListeners.serverConnection) {
                            serverConnectionListener.chatPackageReceived(chatPackage);
                        }
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
