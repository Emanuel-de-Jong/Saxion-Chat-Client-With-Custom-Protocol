package chatapp.server;

import chatapp.client.ClientGlobals;
import chatapp.server.models.Client;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerApp {

    public static void main(String[] args) {
        ServerApp serverApp = new ServerApp();
        serverApp.start(Globals.port);
    }

    private ServerSocket serverSocket;
    private ServerGlobals globals;

    public void start(int port) {
        try {
            globals = new ServerGlobals();

            serverSocket = new ServerSocket(port);

            globals.groups.put(Globals.publicGroupName, new Group(Globals.publicGroupName, globals));

            new Thread(() -> {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        Client client = new Client(socket);
                        ClientPackageHandler packageHandler = new ClientPackageHandler(client, globals);
                        ClientPinger pinger = new ClientPinger(client, globals);

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
