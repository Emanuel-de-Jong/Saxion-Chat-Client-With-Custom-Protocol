package chatapp.client;

import chatapp.client.gui.AddGroupDialog;
import chatapp.client.gui.AddUserDialog;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.MainFrame;

public class ClientApp {

    public static void main(String[] args) {
        new ClientApp();
    }

    public ClientApp() {
        new MainFrame();
//        new ServerConnection();
//        new LogInDialog();
        new AddGroupDialog();
//        new AddUserDialog();
    }

}
