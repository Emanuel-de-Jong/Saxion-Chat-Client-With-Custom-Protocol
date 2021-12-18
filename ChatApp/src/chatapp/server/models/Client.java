package chatapp.server.models;

import chatapp.server.ClientPackageHandler;
import chatapp.server.ClientPinger;

import java.net.Socket;

public class Client {

    private String name = "";
    private final Socket socket;
    private ClientPackageHandler packageHandler;
    private ClientPinger pinger;


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

    public ClientPackageHandler getPackageHandler() {
        return packageHandler;
    }

    public void setPackageHandler(ClientPackageHandler packageHandler) {
        this.packageHandler = packageHandler;
    }

    public ClientPinger getPinger() {
        return pinger;
    }

    public void setPinger(ClientPinger pinger) {
        this.pinger = pinger;
    }

}
