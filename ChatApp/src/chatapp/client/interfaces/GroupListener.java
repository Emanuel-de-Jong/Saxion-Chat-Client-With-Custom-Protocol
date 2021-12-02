package chatapp.client.interfaces;

import chatapp.shared.models.Group;
import chatapp.shared.models.Message;

public interface GroupListener {

    void messageAdded(Group group, Message message);

}
