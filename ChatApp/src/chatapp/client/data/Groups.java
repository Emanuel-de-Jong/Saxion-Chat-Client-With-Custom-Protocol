package chatapp.client.data;

import chatapp.client.interfaces.GroupsListener;
import chatapp.client.models.Group;
import chatapp.client.models.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class Groups {

    public static ArrayList<GroupsListener> listeners = new ArrayList<>();

    private static HashMap<String, Group> groups = new HashMap<>();


    public static HashMap<String, Group> getGroups() {
        return groups;
    }

    public static void addGroup(Group group) {
        groups.put(group.getName(), group);
        listeners.forEach(l -> l.groupAdded(group));
    }

}
