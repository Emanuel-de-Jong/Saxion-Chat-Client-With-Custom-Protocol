package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.shared.Globals;
import chatapp.shared.Listeners;
import chatapp.shared.models.User;

public class ClientGlobals extends Globals {

    public static String ip = "127.0.0.1";

    public ClientListeners clientListeners = new ClientListeners();

    public SystemHelper systemHelper;
    public User currentUser;
    public Groups groups;
    public Users users;

}
