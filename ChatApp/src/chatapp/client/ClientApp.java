package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.mainframe.MainFrame;
import chatapp.client.interfaces.LogInDialogListener;
import chatapp.shared.Globals;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ConnPackage;
import chatapp.shared.models.chatpackages.ErPackage;
import chatapp.shared.models.chatpackages.GrpsPackage;
import chatapp.shared.models.chatpackages.UsrsPackage;

import java.util.HashMap;
import java.util.function.Consumer;

public class ClientApp implements LogInDialogListener {

    private String testUserName;
    private ClientGlobals globals;
    private ServerConnection serverConnection;
    private MainFrame mainFrame;
    private LogInDialog logInDialog;

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
        globals.systemHelper = new SystemHelper(globals);
        globals.users = new Users(globals);
        globals.groups = new Groups(globals);

        globals.clientListeners.logInDialog.add(this);

        serverConnection = new ServerConnection(globals);

        if (!Globals.testing) {
            logInDialog = new LogInDialog(globals, "Initial");
            System.out.println(logInDialog);
        } else {
            globals.currentUser = new User(testUserName, false, globals);
            serverConnection.sendPackage(new ConnPackage(testUserName));
            step2();
        }
    }

    private void step2() {
        mainFrame = new MainFrame(globals);
        serverConnection.sendPackage(new UsrsPackage());
        serverConnection.sendPackage(new GrpsPackage());
    }

    private final HashMap<Integer, Consumer<String>> logInFails = new HashMap<>() {{
       put(-1,error -> logInDialog.showError("An error has occurred."));
       put(2,error -> logInDialog.showError("Invalid username format."));
       put(24,error -> logInDialog.showError("Username already exists"));
       put(25,error -> logInDialog.showError("Username or Password incorrect"));
    }};
    @Override
    public void logIn(String username, String password) {
        serverConnection.sendPackage(
                new ConnPackage(username,password),
                username,
                () -> logInDialog.close(),
                logInFails
            );
    }

    @Override
    public void logInDialogClosed(String name, String username, String password) {
        if (name.equals("Initial")) {
            System.out.println("C: ClientApp logInDialogClosed " + name + " " + username + " " + password);
            step2();
        }
    }


}
