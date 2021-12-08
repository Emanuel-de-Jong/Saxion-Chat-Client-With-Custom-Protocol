package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;
import chatapp.shared.models.chatpackages.ChatPackage;

public interface ServerConnectionListener extends Listener {

    void chatPackageReceived(ChatPackage chatPackage);

}
