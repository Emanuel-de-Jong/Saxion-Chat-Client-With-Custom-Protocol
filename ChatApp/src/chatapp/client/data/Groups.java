package chatapp.client.data;

import chatapp.client.ClientGlobals;
import chatapp.client.ServerConnection;
import chatapp.client.interfaces.GroupsListener;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.Globals;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.BcstPackage;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.GrpPackage;
import chatapp.shared.models.chatpackages.GrpsPackage;

import java.util.ArrayList;
import java.util.HashMap;

public class Groups extends HashMap<String, Group> implements ServerConnectionListener {

    private final ClientGlobals globals;


    public Groups(ClientGlobals globals) {
        this.globals = globals;
        globals.clientListeners.serverConnection.add(this);
    }


    public void setJoined(boolean joined) {
        for (Group group : values()) {
            group.setJoined(joined);
        }
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
        globals.clientListeners.groups.forEach(l -> l.groupAdded(group));
    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        switch (chatPackage.getType()) {
            case GRP -> addNewGroup((GrpPackage) chatPackage);
            case GRPS -> addNewGroups((GrpsPackage) chatPackage);
            case BCST -> addNewMessage((BcstPackage) chatPackage);
        }
    }

    public void addNewGroup(GrpPackage grpPackage) {
        System.out.println("Groups chatPackageReceived " + grpPackage);
        add(new Group(grpPackage.getGroupName(), globals));
    }

    public void addNewGroups(GrpsPackage grpsPackage) {
        System.out.println("Groups chatPackageReceived " + grpsPackage);
        for (String groupName : grpsPackage.getGroupNames()) {
            Group group = new Group(groupName, globals);
            add(group);

            if (groupName.equals(Globals.publicGroupName)) {
                group.setJoined(true);
            }
        }
    }

    public void addNewMessage(BcstPackage bcstPackage) {
        System.out.println("Groups chatPackageReceived " + bcstPackage);
        Group group = this.get(bcstPackage.getGroupName());
        User sender;
        if (bcstPackage.getSender().equals(globals.currentUser.getName())) {
            sender = globals.currentUser;
        } else {
            sender = globals.users.get(bcstPackage.getSender());
        }
        group.addMessage(new Message(bcstPackage.getMessage(), sender, group));

    }
}
