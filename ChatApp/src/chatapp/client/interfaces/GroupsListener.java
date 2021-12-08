package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;
import chatapp.shared.models.Group;

public interface GroupsListener extends Listener {

    void groupAdded(Group group);

}
