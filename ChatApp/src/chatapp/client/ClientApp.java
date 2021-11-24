package chatapp.client;

import chatapp.client.gui.MainFrame;

public class ClientApp {

    public static void main(String[] args) {
        new ClientApp();
    }

    public ClientApp() {
        new MainFrame();
        new ServerConnection();
    }

}
