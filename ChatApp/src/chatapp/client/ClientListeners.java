package chatapp.client;

import chatapp.client.interfaces.*;
import chatapp.shared.Listeners;

import java.util.ArrayList;

public class ClientListeners extends Listeners {

    public ArrayList<AddUserDialogListener> addUserDialog = new ArrayList<>();
    public ArrayList<AddGroupDialogListener> addGroupDialog = new ArrayList<>();
    public ArrayList<UsersListener> users = new ArrayList<>();
    public ArrayList<GroupsListener> groups = new ArrayList<>();
    public ArrayList<LogInDialogListener> logInDialog = new ArrayList<>();
    public ArrayList<ChatPanelListener> chatPanel = new ArrayList<>();
    public ArrayList<ServerConnectionListener> serverConnection = new ArrayList<>();
    public ArrayList<SystemHelperListener> systemHelper = new ArrayList<>();

}
