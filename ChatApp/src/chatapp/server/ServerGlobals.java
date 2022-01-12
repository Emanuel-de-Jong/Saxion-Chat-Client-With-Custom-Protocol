package chatapp.server;

import chatapp.client.data.Groups;
import chatapp.server.clientthreads.filetransfer.FileTransferHandler;
import chatapp.server.data.Clients;
import chatapp.server.models.AuthUser;
import chatapp.server.storage.AuthUsersStorage;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;

import java.util.HashMap;

public class ServerGlobals extends Globals {

    public static final double VERSION = 1.5;
    public static final boolean PING = true;

    public Clients clients = new Clients();
    public HashMap<String, User> users = new HashMap<>();
    public HashMap<String, AuthUser> authenticatedUsers = new AuthUsersStorage();
    public HashMap<String, Group> groups = new HashMap<>();
    public HashMap<byte[], FileTransferHandler> fileTransferHandlers = new HashMap<>();
}
