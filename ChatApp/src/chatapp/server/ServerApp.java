package chatapp.server;

import chatapp.server.clientthreads.ClientHandler;
import chatapp.server.models.Client;
import chatapp.shared.Globals;
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
    private ServerGlobals globals;

    public void start(int port) {
        try {

            globals = new ServerGlobals();

            System.out.println("Listening on port: " + port);
            serverSocket = new ServerSocket(port);

            globals.groups.put(Globals.PUBLIC_GROUP_NAME, new Group(Globals.PUBLIC_GROUP_NAME, globals));

            new Thread(() -> {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();

                        Client client = new Client(socket);
                        ClientHandler packageHandler = new ClientHandler(client, globals);
                        client.setHandler(packageHandler);

                        globals.clients.add(client);

                        packageHandler.start();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
