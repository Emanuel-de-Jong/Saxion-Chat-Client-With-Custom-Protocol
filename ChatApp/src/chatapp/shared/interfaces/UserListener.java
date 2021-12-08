package chatapp.shared.interfaces;

import chatapp.shared.models.Message;
import chatapp.shared.models.User;

public interface UserListener extends Listener {

    void privateMessageAdded(User user, Message message);

    void chatAddedSet(User user, boolean chatAdded);

}
