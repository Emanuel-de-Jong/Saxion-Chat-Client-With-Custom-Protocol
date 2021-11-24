package chatapp.client;

import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.ArrayList;

public class ServerConnection {

    public static ArrayList<ServerConnectionListener> listeners = new ArrayList<>();

    public ServerConnection() {
        ChatPackage chatPackage = ChatPackageHelper.deserializeClient("BCST Sender Group This is a message!");
        listeners.forEach(l -> l.chatPackageReceived(chatPackage));
    }

}
