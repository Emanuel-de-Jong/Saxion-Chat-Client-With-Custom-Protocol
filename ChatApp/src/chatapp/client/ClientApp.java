package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.mainframe.MainFrame;
import chatapp.client.interfaces.LogInDialogListener;
import chatapp.shared.Globals;
import chatapp.shared.SystemHelper;
import chatapp.shared.enums.Flag;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

import java.util.HashMap;
import java.util.function.Consumer;

public class ClientApp implements LogInDialogListener {

    private String testUserName;
    private ClientGlobals globals;
    private ServerConnection serverConnection;
    private MainFrame mainFrame;
    private LogInDialog logInDialog;


    public static void main(String[] args) {
        if (args.length >= 1) {
            String[] address = args[0].split(":");
            Globals.IP = address[0];
            if (address.length >= 2) {
                Globals.PORT = Integer.parseInt(address[1]);
            }
        }
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

        if (!Globals.TESTING) {
            logInDialog = new LogInDialog(globals, "Initial");
        } else {
            globals.currentUser = new User(testUserName, false, globals);
            serverConnection.sendPackage(new ConnPackage(testUserName));
            step2();
        }
    }

    private void step2() {
        mainFrame = new MainFrame(globals);
        serverConnection.sendPackage(new FlagPackage(Flag.GetNewUsers));
        serverConnection.sendPackage(new UsrsPackage());
        serverConnection.sendPackage(new GrpsPackage());
    }

    private final HashMap<Integer, Consumer<String>> logInFails = new HashMap<>() {{
       put(-1, error ->
               logInDialog.showError("An error has occurred."));
       put(ErPackage.alreadyLoggedIn.getCode(), error ->
                logInDialog.showError("You are already logged in somewhere else."));
       put(ErPackage.userNameInvalid.getCode(), error ->
               logInDialog.showError("Invalid username format."));
       put(ErPackage.userNameExists.getCode(), error ->
               logInDialog.showError("Username already exists"));
       put(ErPackage.logInInvalid.getCode(), error ->
               logInDialog.showError("Username or Password incorrect"));
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
            globals.systemHelper.log("ClientApp logInDialogClosed " + name + " " + username + " " + password);
            step2();
        }
    }

}
