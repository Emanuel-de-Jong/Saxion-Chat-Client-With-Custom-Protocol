package chatapp.server;

import chatapp.server.Data.Clients;
import chatapp.server.models.AuthUser;
import chatapp.server.storage.AuthUsersStorage;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;

import java.util.HashMap;

public class ServerGlobals extends Globals {

    public static final double version = 2.0;

    public Clients clients = new Clients();
    public HashMap<String, User> users = new HashMap<>();
    public HashMap<String, AuthUser> authenticatedUsers = new AuthUsersStorage();
    public HashMap<String, Group> groups = new HashMap<>();

}
