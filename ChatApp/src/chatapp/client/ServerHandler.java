package chatapp.client;

import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            new ServerPackageHandler(serverConnection, globals);

            String packageStr;
            while (!Thread.currentThread().isInterrupted() &&
                    !"false".equals(packageStr = in.readLine())) {
                ChatPackage chatPackage;
                try {
                    chatPackage = ChatPackageHelper.deserialize(packageStr, true);
                } catch (IllegalArgumentException ex) {
                    continue;
                }

                if (chatPackage == null) continue;
                globals.systemHelper.log(chatPackage.toString(), true);


                for (ServerConnectionListener serverConnectionListener : globals.clientListeners.serverConnection) {
                    try {
                        serverConnectionListener.chatPackageReceived(chatPackage);
                    } catch (Exception e) {
                        e.printStackTrace();
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
