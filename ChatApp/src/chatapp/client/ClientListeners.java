package chatapp.client;

import chatapp.client.interfaces.*;
import chatapp.shared.Listeners;

import java.util.ArrayList;
import java.util.List;

public class ClientListeners extends Listeners {

    public final List<AddUserDialogListener> addUserDialog = new ArrayList<>();
    public final List<AddGroupDialogListener> addGroupDialog = new ArrayList<>();
    public final List<UsersListener> users = new ArrayList<>();
    public final List<GroupsListener> groups = new ArrayList<>();
    public final List<LogInDialogListener> logInDialog = new ArrayList<>();
    public final List<ChatPanelListener> chatPanel = new ArrayList<>();
    public final List<ServerConnectionListener> serverConnection = new ArrayList<>();
    public final List<UploadListener> uploads = new ArrayList<>();
    public final List<DownloadListener> downloads = new ArrayList<>();

}
