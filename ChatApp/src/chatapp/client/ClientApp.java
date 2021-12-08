package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.MainFrame;
import chatapp.client.interfaces.LogInDialogListener;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ConnPackage;
import chatapp.shared.models.chatpackages.GrpsPackage;
import chatapp.shared.models.chatpackages.UsrsPackage;

public class ClientApp implements LogInDialogListener {

    private String testUserName;
    private ClientGlobals globals;
    private ServerConnection serverConnection;
    private MainFrame mainFrame;


    public static void main(String[] args) {
        new ClientApp();
    }

    public ClientApp() {
        step1();
    }

    public ClientApp(String testUserName) {
        this.testUserName = testUserName;
        step1();
    }


    public ClientGlobals getGlobals() {
        return globals;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }


    private void step1() {
        globals = new ClientGlobals();
        if (testUserName != null) {
            globals.testing = true;
        }

        LogInDialog.listeners.add(this);

        globals.users = new Users(globals);
        globals.groups = new Groups(globals);
        serverConnection = new ServerConnection();

        if (!globals.testing) {
            new LogInDialog(globals, "Initial");
        } else {
            globals.currentUser = new User(testUserName);
            step2();
        }
    }

    private void step2() {
        serverConnection.sendPackage(new ConnPackage(globals.currentUser.getName()));
        mainFrame = new MainFrame(globals);
        serverConnection.sendPackage(new UsrsPackage());
        serverConnection.sendPackage(new GrpsPackage());
    }

    @Override
    public void logInDialogClosed(String name) {
        if (name.equals("Initial")) {
            System.out.println("ClientApp logInDialogClosed");
            step2();
        }
    }

}
