package chatapp.client;

import chatapp.client.interfaces.*;
import chatapp.shared.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientListeners extends Listeners {

    public final List<AddUserDialogListener> addUserDialog = Collections.synchronizedList(new ArrayList<>()) ;
    public final List<AddGroupDialogListener> addGroupDialog = Collections.synchronizedList(new ArrayList<>()) ;
    public final List<UsersListener> users = Collections.synchronizedList(new ArrayList<>()) ;
    public final List<GroupsListener> groups = Collections.synchronizedList(new ArrayList<>()) ;
    public final List<LogInDialogListener> logInDialog = Collections.synchronizedList(new ArrayList<>()) ;
    public final List<ChatPanelListener> chatPanel = Collections.synchronizedList(new ArrayList<>()) ;
    public final List<ServerConnectionListener> serverConnection = Collections.synchronizedList(new ArrayList<>()) ;
    public final List<SystemHelperListener> systemHelper = Collections.synchronizedList(new ArrayList<>()) ;

}
