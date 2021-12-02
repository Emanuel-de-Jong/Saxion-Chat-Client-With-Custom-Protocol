package chatapp.client.interfaces;

import chatapp.client.models.Group;
import chatapp.client.models.Message;

import java.util.ArrayList;

public interface GroupListener {

    void messageAdded(Group group, Message message);

}
