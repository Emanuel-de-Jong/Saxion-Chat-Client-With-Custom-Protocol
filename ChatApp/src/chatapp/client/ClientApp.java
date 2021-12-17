package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.MainFrame;
import chatapp.client.interfaces.LogInDialogListener;
import chatapp.server.models.AuthUser;
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

    public MainFrame getMainFrame() {
        return mainFrame;
    }


    private void step1() {
        globals = new ClientGlobals();

        globals.clientListeners.logInDialog.add(this);

        globals.users = new Users(globals);
        globals.groups = new Groups(globals);
        serverConnection = new ServerConnection(globals);

        if (!globals.testing) {
            new LogInDialog(globals, "Initial");
        } else {
            globals.currentUser = new User(testUserName, globals);
            serverConnection.sendPackage(new ConnPackage(testUserName));
            step2();
        }
    }

    private void step2() {
        mainFrame = new MainFrame(globals);
        serverConnection.sendPackage(new UsrsPackage());
        serverConnection.sendPackage(new GrpsPackage());
    }

    @Override
    public void logInDialogClosed(String name, String username, String password) {
        if (name.equals("Initial")) {
            System.out.println("ClientApp logInDialogClosed " + name);
            serverConnection.sendPackage(new ConnPackage(username, password));
            step2();
        }
    }

}
