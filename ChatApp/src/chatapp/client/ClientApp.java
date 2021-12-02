package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.MainFrame;
import chatapp.client.interfaces.LogInDialogListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ConnPackage;
import chatapp.shared.models.chatpackages.GrpsPackage;
import chatapp.shared.models.chatpackages.UsrsPackage;

public class ClientApp implements LogInDialogListener {

    private String autoLoginUserName;
    private Globals globals;
    private ServerConnection serverConnection;
    private MainFrame mainFrame;


    public static void main(String[] args) {
        new ClientApp();
    }

    public ClientApp() {
        step1();
    }

    public ClientApp(String autoLoginUserName) {
        this.autoLoginUserName = autoLoginUserName;
        step1();
    }


    public Globals getGlobals() {
        return globals;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }


    private void step1() {
        globals = new Globals();

        globals.users = new Users(globals);
        globals.groups = new Groups(globals);
        serverConnection = new ServerConnection();

        if (autoLoginUserName == null) {
            LogInDialog.listeners.add(this);
            new LogInDialog(globals);
        } else {
            globals.currentUser = new User(autoLoginUserName);
            step2();
        }
    }

    private void step2() {
        serverConnection.sendPackage(new ConnPackage(globals.currentUser.getName()));
        mainFrame = new MainFrame(globals);
        serverConnection.sendPackage(new UsrsPackage());
        serverConnection.sendPackage(new GrpsPackage());
    }

    private void addExampleData() {
        User u1 = new User("u1");
        u1.addPrivateMessage(new Message("u1m1", u1));
        u1.addPrivateMessage(new Message("u1m2", u1));
        globals.users.addUser(u1);

        User u2 = new User("u2");
        u2.addPrivateMessage(new Message("u2m1", u2));
        u2.addPrivateMessage(new Message("u2m2", u2));
        globals.users.addUser(u2);

        User u3 = new User("u3");
        globals.users.addUser(u3);

        Group g1 = new Group("g1");
        g1.addUser(u1);
        g1.addUser(u2);
        g1.addMessage(new Message("g1u1m1", u1, g1));
        g1.addMessage(new Message("g1u2m1", u2, g1));
        g1.addMessage(new Message("g1u1m2", u1, g1));
        globals.groups.addGroup(g1);

        Group g2 = new Group("g2");
        g2.addUser(u2);
        g2.addUser(u3);
        g2.addMessage(new Message("g2u3m1", u3, g2));
        g2.addMessage(new Message("g2u2m1", u2, g2));
        globals.groups.addGroup(g2);
    }

    @Override
    public void logInDialogClosed() {
        System.out.println("ClientApp logInDialogClosed");
        step2();
    }

}
