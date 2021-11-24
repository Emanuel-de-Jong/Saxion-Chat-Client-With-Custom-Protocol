package chatapp.client;

import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.ChatPackageHelper;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.ArrayList;

public class ServerConnection {

    public static ArrayList<ServerConnectionListener> listeners = new ArrayList<>();

    public ServerConnection() {
        ChatPackage chatPackage1 = ChatPackageHelper.deserialize("BCST Sender Group This is a message!", true);
        listeners.forEach(l -> l.chatPackageReceived(chatPackage1));

        ChatPackage chatPackage2 = ChatPackageHelper.deserialize("CONN userName", true);
        listeners.forEach(l -> l.chatPackageReceived(chatPackage2));

        ChatPackage chatPackage3 = ChatPackageHelper.deserialize("CONN UserName Password", true);
        listeners.forEach(l -> l.chatPackageReceived(chatPackage3));
    }

}
