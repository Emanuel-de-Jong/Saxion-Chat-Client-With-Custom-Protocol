package chatapp.server;

import chatapp.server.clientthreads.ClientHandler;
import chatapp.server.clientthreads.filetransfer.FileTransferHandler;
import chatapp.server.models.Client;
import chatapp.shared.Globals;
import chatapp.shared.SystemHelper;
import chatapp.shared.models.Group;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    public static void main(String[] args) {
        if (args.length >= 1) {
            Globals.PORT = Integer.parseInt(args[0]);
        }
        ServerApp serverApp = new ServerApp();
        serverApp.start(Globals.PORT);
    }

    private ServerSocket serverSocket;
    private ServerSocket filetransferServerSocket;
    private ServerGlobals globals;

    public void start(int port) {
        try {

            globals = new ServerGlobals();
            globals.systemHelper = new SystemHelper(globals);

            globals.systemHelper.log("Listening on port " + port);
            serverSocket = new ServerSocket(port);

            globals.groups.put(Globals.PUBLIC_GROUP_NAME, new Group(Globals.PUBLIC_GROUP_NAME, globals));

            new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();

                        Client client = new Client(socket);
                        ClientHandler packageHandler = new ClientHandler(client, globals);
                        client.setHandler(packageHandler);

                        globals.clients.add(client);

                        packageHandler.start();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }).start();

            filetransferServerSocket = new ServerSocket(port + 1);
            globals.systemHelper.log("Listening on port (files) " + (port + 1));

            new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = filetransferServerSocket.accept();
                        FileTransferHandler fileTransferHandler = new FileTransferHandler(socket, globals);
                        fileTransferHandler.start();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
