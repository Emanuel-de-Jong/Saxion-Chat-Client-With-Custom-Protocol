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


    /**
     * hashmap of all the groups
     * @param globals
     */
    public Groups(ClientGlobals globals) {
        this.globals = globals;
        globals.clientListeners.serverConnection.add(this);
    }

    /**
     * set all the groups joined status
     * @param joined
     */
    public void setJoined(boolean joined) {
        for (Group group : values()) {
            group.setJoined(joined);
        }
    }

    /**
     * get all the groups that you are in or not in
     * @param joined
     * @return
     */
    public ArrayList<Group> valuesByJoined(boolean joined) {
        ArrayList<Group> filtered = new ArrayList<>();
        for (Group group : values()) {
            if (group.isJoined() == joined) {
                filtered.add(group);
            }
        }
        return filtered;
    }

    /**
     * add a new group
     * @param group
     */
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
            case BCST -> addNewMessageToPublicGroup((BcstPackage) chatPackage);
            case GBCST -> addNewMessage((GbcstPackage) chatPackage);
        }
    }

    /**
     * add a new group
     * @param grpPackage
     */
    public void addNewGroup(GrpPackage grpPackage) {
        globals.systemHelper.log("Groups addNewGroup " + grpPackage);
        add(new Group(grpPackage.getGroupName(), globals));
    }

    /**
     * add multiple new groups
     * @param grpsPackage
     */
    public void addNewGroups(GrpsPackage grpsPackage) {
        globals.systemHelper.log("Groups addNewGroups " + grpsPackage);
        for (String groupName : grpsPackage.getGroupNames()) {
            Group group = new Group(groupName, globals);
            add(group);

            if (groupName.equals(Globals.PUBLIC_GROUP_NAME)) {
                group.setJoined(true);
            }
        }
    }

    /**
     * display group joined message
     * @param jgrpPackage
     */
    public void addGroupJoinedMessage(JgrpPackage jgrpPackage) {
        Group group = this.get(jgrpPackage.getGroupName());
        if (group != null) {
            globals.systemHelper.log("Groups addGroupJoinedMessage " + jgrpPackage);
            group.addMessage(new Message(jgrpPackage.getUserName() + " joined!", null, group));
        }
    }

    /**
     * display a group left message
     * @param lgrpPackage
     */
    public void addGroupLeftMessage(LgrpPackage lgrpPackage) {
        Group group = this.get(lgrpPackage.getGroupName());
        if (group != null) {
            globals.systemHelper.log("Groups addGroupLeftMessage " + lgrpPackage);
            group.addMessage(new Message(lgrpPackage.getUserName() + " left!", null, group));
        }
    }

    /**
     * hide group if group timedout
     * @param gtmtPackage
     */
    public void groupTimeout(GtmtPackage gtmtPackage) {
        Group group = this.get(gtmtPackage.getGroupName());
        if (group != null) {
            globals.systemHelper.log("Groups groupTimeout " + gtmtPackage);
            group.setJoined(false);
        }
    }

    /**
     * add a message to the public group
     * @param bcstPackage
     */
    public void addNewMessageToPublicGroup(BcstPackage bcstPackage) {
        globals.systemHelper.log("Groups addNewMessageToPublicGroup " + bcstPackage);
        User sender;
        if (bcstPackage.getSender().equals(globals.currentUser.getName())) {
            sender = globals.currentUser;
        } else {
            sender = globals.users.get(bcstPackage.getSender());
        }
        Group group = this.get(Globals.PUBLIC_GROUP_NAME);
        group.addMessage(new Message(bcstPackage.getMessage(), sender, group));
    }

    /**
     * add a new message to a normal group
     * @param gbcstPackage
     */
    public void addNewMessage(GbcstPackage gbcstPackage) {
        globals.systemHelper.log("Groups addNewMessage " + gbcstPackage);
        User sender;
        if (gbcstPackage.getSender().equals(globals.currentUser.getName())) {
            sender = globals.currentUser;
        } else {
            sender = globals.users.get(gbcstPackage.getSender());
        }
        Group group = this.get(gbcstPackage.getGroupName());
        group.addMessage(new Message(gbcstPackage.getMessage(), sender, group));
    }

}
