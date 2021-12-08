package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;
import chatapp.shared.models.Message;

public interface MainFrameListener extends Listener {

    void sendMessage(Message message);

}
