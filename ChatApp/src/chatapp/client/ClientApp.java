package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.MainFrame;
import chatapp.client.interfaces.LogInDialogListener;
import chatapp.client.interfaces.MainFrameListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ConnPackage;
import chatapp.shared.models.chatpackages.GrpsPackage;
import chatapp.shared.models.chatpackages.UsrsPackage;

public class ClientApp implements LogInDialogListener {

    private ServerConnection serverConnection;

    public static void main(String[] args) {
        new ClientApp();
    }

    public ClientApp() {
        LogInDialog.listeners.add(this);
        step1();
    }

    private void step1() {
        serverConnection = new ServerConnection();
        new LogInDialog();
    }

    private void step2() {
        serverConnection.sendPackage(new ConnPackage(Config.currentUser.getName()));
        new MainFrame();
        serverConnection.sendPackage(new UsrsPackage());
        serverConnection.sendPackage(new GrpsPackage());
    }

    private void addExampleData() {
        User u1 = new User("u1");
        u1.addPrivateMessage(new Message("u1m1", u1));
        u1.addPrivateMessage(new Message("u1m2", u1));
        Users.instance.addUser(u1);

        User u2 = new User("u2");
        u2.addPrivateMessage(new Message("u2m1", u2));
        u2.addPrivateMessage(new Message("u2m2", u2));
        Users.instance.addUser(u2);

        User u3 = new User("u3");
        Users.instance.addUser(u3);

        Group g1 = new Group("g1");
        g1.addUser(u1);
        g1.addUser(u2);
        g1.addMessage(new Message("g1u1m1", u1, g1));
        g1.addMessage(new Message("g1u2m1", u2, g1));
        g1.addMessage(new Message("g1u1m2", u1, g1));
        Groups.instance.addGroup(g1);

        Group g2 = new Group("g2");
        g2.addUser(u2);
        g2.addUser(u3);
        g2.addMessage(new Message("g2u3m1", u3, g2));
        g2.addMessage(new Message("g2u2m1", u2, g2));
        Groups.instance.addGroup(g2);
    }

    @Override
    public void logInDialogClosed() {
        System.out.println("ClientApp logInDialogClosed");
        step2();
    }

}
