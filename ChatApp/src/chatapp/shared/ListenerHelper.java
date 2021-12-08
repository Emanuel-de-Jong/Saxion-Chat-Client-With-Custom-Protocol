package chatapp.shared;

import chatapp.shared.interfaces.Listener;

import java.util.ArrayList;

public class ListenerHelper {

    public static void sub(Listener listener, ArrayList<Listener> listeners) {
        if (listeners.contains(listener))
            listeners.add(listener);
    }

}
