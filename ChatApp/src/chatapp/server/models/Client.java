package chatapp.server.models;

import chatapp.server.clientthreads.ClientHandler;
import chatapp.shared.models.User;

import java.net.Socket;

public class Client {

    private User user;
    private final Socket socket;
    private ClientHandler handler;


    public Client(Socket socket) {
        this.socket = socket;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return user.getName();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }

}
