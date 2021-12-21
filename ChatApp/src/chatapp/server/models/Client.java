package chatapp.server.models;

import chatapp.server.ClientIdleChecker;
import chatapp.server.ClientHandler;
import chatapp.server.ClientPinger;

import java.net.Socket;

public class Client {

    private String name = "";
    private final Socket socket;
    private ClientHandler handler;


    public Client(Socket socket) {
        this.socket = socket;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public ClientHandler getHandler() {
        return handler;
    }

    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }

}
