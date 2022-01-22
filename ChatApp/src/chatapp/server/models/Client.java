package chatapp.server.models;

import chatapp.server.clientthreads.ClientHandler;
import chatapp.shared.enums.Flag;
import chatapp.shared.models.User;

import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private User user;
    private final Socket socket;
    private ClientHandler handler;
    private final ArrayList<Flag> flags = new ArrayList<>();


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
        if (user == null) return null;
        return user.getName();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setHandler(ClientHandler handler) {
        this.handler = handler;
    }

    public boolean containsFlag(Flag flag) {
        return flags.contains(flag);
    }

    public void addFlag(Flag flag) {
        flags.add(flag);
    }

}
