package chatapp.client.data;

import chatapp.client.ClientGlobals;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.Globals;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.*;

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
            case JGRP -> addGroupJoinedMessage((JgrpPackage) chatPackage);
            case LGRP -> addGroupLeftMessage((LgrpPackage) chatPackage);
            case GTMT -> groupTimeout((GtmtPackage) chatPackage);
            case BCST -> addNewMessage((BcstPackage) chatPackage);
        }
    }

    public void addNewGroup(GrpPackage grpPackage) {
        System.out.println("C: Groups addNewGroup " + grpPackage);
        add(new Group(grpPackage.getGroupName(), globals));
    }

    public void addNewGroups(GrpsPackage grpsPackage) {
        System.out.println("C: Groups addNewGroups " + grpsPackage);
        for (String groupName : grpsPackage.getGroupNames()) {
            Group group = new Group(groupName, globals);
            add(group);

            if (groupName.equals(Globals.publicGroupName)) {
                group.setJoined(true);
            }
        }
    }

    public void addGroupJoinedMessage(JgrpPackage jgrpPackage) {
        Group group = this.get(jgrpPackage.getGroupName());
        if (group != null) {
            System.out.println("C: Groups addGroupJoinedMessage " + jgrpPackage);
            group.addMessage(new Message(jgrpPackage.getUserName() + " joined!", null, group));
        }
    }

    public void addGroupLeftMessage(LgrpPackage lgrpPackage) {
        Group group = this.get(lgrpPackage.getGroupName());
        if (group != null) {
            System.out.println("C: Groups addGroupLeftMessage " + lgrpPackage);
            group.addMessage(new Message(lgrpPackage.getUserName() + " left!", null, group));
        }
    }

    public void groupTimeout(GtmtPackage gtmtPackage) {
        Group group = this.get(gtmtPackage.getGroupName());
        if (group != null) {
            System.out.println("C: Groups groupTimeout " + gtmtPackage);
            group.setJoined(false);
        }
    }

    public void addNewMessage(BcstPackage bcstPackage) {
        System.out.println("C: Groups addNewMessage " + bcstPackage);
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
