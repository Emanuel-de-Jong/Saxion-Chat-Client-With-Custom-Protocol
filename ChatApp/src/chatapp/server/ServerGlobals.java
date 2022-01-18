package chatapp.server;

import chatapp.server.data.Clients;
import chatapp.server.data.FileTransferHandlers;
import chatapp.server.models.AuthUser;
import chatapp.server.models.FileTransfer;
import chatapp.server.storage.AuthUsersStorage;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerGlobals extends Globals {

    public static final double VERSION = 1.5;
    public static final boolean PING = true;

    public final Clients clients = new Clients();
    public final FileTransferHandlers fileTransferHandlers = new FileTransferHandlers();
    public final HashMap<String, User> users = new HashMap<>();
    public final HashMap<String, AuthUser> authenticatedUsers = new AuthUsersStorage();
    public final HashMap<String, Group> groups = new HashMap<>();
    public final ArrayList<FileTransfer> fileTransfers = new ArrayList<>();

}
