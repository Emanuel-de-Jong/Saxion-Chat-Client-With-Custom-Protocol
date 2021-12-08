package chatapp.client.data;

import chatapp.client.ClientGlobals;
import chatapp.client.ServerConnection;
import chatapp.client.interfaces.GroupsListener;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.chatpackages.BcstPackage;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.GrpPackage;
import chatapp.shared.models.chatpackages.GrpsPackage;

import java.util.ArrayList;
import java.util.HashMap;

public class Groups extends HashMap<String, Group> implements ServerConnectionListener {

    public static ArrayList<GroupsListener> listeners = new ArrayList<>();

    private final ClientGlobals globals;


    public Groups(ClientGlobals globals) {
        this.globals = globals;
        ServerConnection.listeners.add(this);
    }


    public ArrayList<Group> valuesByJoined(boolean joined) {
        ArrayList<Group> filtered = new ArrayList<>();
        for (Group group : values()) {
            if (group.isJoined() == joined) {
                filtered.add(group);
            }
        }
        return filtered;
    }

    public void add(Group group) {
        put(group.getName(), group);
        listeners.forEach(l -> l.groupAdded(group));
    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        if (chatPackage.getType() == ChatPackageType.GRP) {
            GrpPackage grpPackage = (GrpPackage) chatPackage;
            System.out.println("Groups chatPackageReceived " + grpPackage);
            add(new Group(grpPackage.getGroupName()));
        } else if (chatPackage.getType() == ChatPackageType.GRPS) {
            GrpsPackage grpsPackage = (GrpsPackage) chatPackage;
            System.out.println("Groups chatPackageReceived " + grpsPackage);
            for (String groupName : grpsPackage.getGroupNames()) {
                add(new Group(groupName));
            }
        } else if (chatPackage.getType() == ChatPackageType.BCST) {
            BcstPackage bcstPackage = (BcstPackage) chatPackage;
            if (!bcstPackage.getSender().equals(globals.currentUser.getName())) {
                System.out.println("Groups chatPackageReceived " + bcstPackage);
                Group group = this.get(bcstPackage.getGroupName());
                group.addMessage(new Message(bcstPackage.getMessage(),
                        globals.users.get(bcstPackage.getSender()),
                        group));
            }
        }
    }

}
