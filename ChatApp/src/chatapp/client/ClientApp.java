package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.client.gui.AddGroupDialog;
import chatapp.client.gui.AddUserDialog;
import chatapp.client.gui.LogInDialog;
import chatapp.client.gui.MainFrame;
import chatapp.client.models.Group;
import chatapp.client.models.Message;
import chatapp.client.models.User;

public class ClientApp {

    public static void main(String[] args) {
        new ClientApp();
    }

    public ClientApp() {
        addExampleData();
        new LogInDialog();
        new MainFrame();
    }

    private void addExampleData() {
        User u1 = new User("u1");
        u1.addPrivateMessage(new Message("u1m1", u1));
        u1.addPrivateMessage(new Message("u1m2", u1));
        Users.addUser(u1);

        User u2 = new User("u2");
        u2.addPrivateMessage(new Message("u2m1", u2));
        u2.addPrivateMessage(new Message("u2m2", u2));
        Users.addUser(u2);

        User u3 = new User("u3");
        Users.addUser(u3);

        Group g1 = new Group("g1");
        g1.addUser(u1);
        g1.addUser(u2);
        g1.addMessage(new Message("g1u1m1", u1));
        g1.addMessage(new Message("g1u2m1", u2));
        g1.addMessage(new Message("g1u1m2", u1));
        Groups.addGroup(g1);


        Group g2 = new Group("g2");
        g2.addUser(u2);
        g2.addUser(u3);
        g2.addMessage(new Message("g2u3m1", u3));
        g2.addMessage(new Message("g2u2m1", u2));
        Groups.addGroup(g2);
    }

}
