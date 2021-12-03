package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.shared.models.User;

public class ClientGlobals {

    public static String ip = "127.0.0.1";
    public static int port = 6666;

    public User currentUser;
    public Groups groups;
    public Users users;

}
