package chatapp.server;

import chatapp.server.Data.Clients;
import chatapp.server.models.AuthUser;
import chatapp.server.storage.AuthUsersStorage;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;

import java.util.HashMap;

public class ServerGlobals extends Globals {

    public static int secondsPerPing = 3;
    public static int secondsForPong = 10;

    public Clients clients = new Clients();
    public HashMap<String, User> users = new HashMap<>();
    public HashMap<String, AuthUser> authenticatedUsers = new AuthUsersStorage();
    public HashMap<String, Group> groups = new HashMap<>();
    public static Object groupsSync = new Object();

}
