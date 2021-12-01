package chatapp.client.interfaces;

import chatapp.client.models.Message;
import chatapp.client.models.User;

public interface UserListener {

    void privateMessageAdded(User user, Message message);

}
