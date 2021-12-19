package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;
import chatapp.shared.models.User;

public interface UsersListener extends Listener {

    void userAdded(User user);

    void userRemoved(User user);

}
