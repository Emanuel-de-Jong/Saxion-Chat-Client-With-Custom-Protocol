package chatapp.client;

import chatapp.client.filetransfer.DownloadHandler;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ErPackage;
import chatapp.shared.models.chatpackages.GbcstPackage;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.PongPackage;
import chatapp.shared.models.chatpackages.filetransfer.DnrqPackage;

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
                    !"false".equals(packageStr = in.readLine())) {
                ChatPackage chatPackage;
                try {
                    chatPackage = ChatPackageHelper.deserialize(packageStr, true);
                } catch (IllegalArgumentException ex) {
                    continue;
                }

                if (chatPackage == null) continue;
                globals.systemHelper.log(chatPackage.toString(), true);

                switch (chatPackage.getType()) {
                    case PING:
                        serverConnection.sendPackage(new PongPackage());
                        break;
                    case DNRQ:
                        DnrqPackage dnrqPackage = (DnrqPackage) chatPackage;
                        new DownloadHandler(
                                globals.users.get(dnrqPackage.getUser()),
                                dnrqPackage.getFileName(),
                                dnrqPackage.getFileSize(),
                                dnrqPackage.getHash(),
                                globals
                        ).start();
                        break; //note to self: odd java behaviour, starts case dscn after download request if break is not here. results in a case where it is impossible to listen for DNRQ package.
                    case DSCN:
                        globals.systemHelper.restart();
                    default:
                        try {
                            for (ServerConnectionListener serverConnectionListener : globals.clientListeners.serverConnection) {
                                serverConnectionListener.chatPackageReceived(chatPackage);
                            }
                        } catch (ConcurrentModificationException cme) {
                            globals.systemHelper.restart();
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
