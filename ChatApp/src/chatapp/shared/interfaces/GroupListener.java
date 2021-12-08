package chatapp.shared.interfaces;

import chatapp.shared.models.Group;
import chatapp.shared.models.Message;

public interface GroupListener extends Listener {

    void messageAdded(Group group, Message message);

    void joinedSet(Group group, boolean joined);

}
