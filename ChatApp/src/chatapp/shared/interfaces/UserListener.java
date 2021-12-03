package chatapp.shared.interfaces;

import chatapp.shared.models.Message;
import chatapp.shared.models.User;

public interface UserListener {

    void privateMessageAdded(User user, Message message);

}
