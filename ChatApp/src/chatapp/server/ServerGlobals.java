package chatapp.server;

import chatapp.server.Data.Clients;
import chatapp.server.models.Client;
import chatapp.shared.Globals;
import chatapp.shared.models.AuthenticatedUser;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerGlobals extends Globals {

    public static int secondsPerPing = 3;
    public static int secondsForPong = 10;

    public Clients clients = new Clients();
    public HashMap<String, User> users = new HashMap<>();
    public HashMap<String, AuthenticatedUser> authenticatedUsers = new HashMap<>();
    public HashMap<String, Group> groups = new HashMap<>();

}
