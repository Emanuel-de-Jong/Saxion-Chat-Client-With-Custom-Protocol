package chatapp.client.data;

import chatapp.client.ServerConnection;
import chatapp.client.interfaces.GroupsListener;
import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.models.Group;
import chatapp.shared.models.Message;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Groups implements ServerConnectionListener {

    public static ArrayList<GroupsListener> listeners = new ArrayList<>();

    public static Groups instance = new Groups();

    private HashMap<String, Group> groups = new HashMap<>();


    public Groups() {
        ServerConnection.listeners.add(this);
    }


    public HashMap<String, Group> getGroups() {
        return groups;
    }

    public Group getGroup(String groupName) {
        return groups.get(groupName);
    }

    public void addGroup(Group group) {
        groups.put(group.getName(), group);
        listeners.forEach(l -> l.groupAdded(group));
    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        if (chatPackage.getType() == ChatPackageType.GRP) {
            GrpPackage grpPackage = (GrpPackage) chatPackage;
            System.out.println("Groups chatPackageReceived " + grpPackage);
            addGroup(new Group(grpPackage.getGroupName()));
        }
        else if (chatPackage.getType() == ChatPackageType.GRPS) {
            GrpsPackage grpsPackage = (GrpsPackage) chatPackage;
            System.out.println("Groups chatPackageReceived " + grpsPackage);
            for (String groupName : grpsPackage.getGroupNames()) {
                addGroup(new Group(groupName));
            }
        }
        else if (chatPackage.getType() == ChatPackageType.JGRP) {
            JgrpPackage jgrpPackage = (JgrpPackage) chatPackage;
            System.out.println("Groups chatPackageReceived " + jgrpPackage);
            Group group = groups.get(jgrpPackage.getGroupName());
            group.addUser(Users.instance.getUser(jgrpPackage.getUserName()));
        }
        else if (chatPackage.getType() == ChatPackageType.BCST) {
            BcstPackage bcstPackage = (BcstPackage) chatPackage;
            System.out.println("Groups chatPackageReceived " + bcstPackage);
            Group group = groups.get(bcstPackage.getGroupName());
            group.addMessage(new Message(bcstPackage.getMessage(),
                    Users.instance.getUser(bcstPackage.getSender()),
                    group));
        }
    }

}
