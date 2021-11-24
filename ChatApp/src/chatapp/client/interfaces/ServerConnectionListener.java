package chatapp.client.interfaces;

import chatapp.shared.models.chatpackages.ChatPackage;

public interface ServerConnectionListener {

    void chatPackageReceived(ChatPackage chatPackage);

}
