package chatapp.shared;

import chatapp.client.interfaces.SystemHelperListener;
import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.interfaces.UserListener;

import java.util.ArrayList;
import java.util.List;

public class Listeners {

    public ArrayList<GroupListener> group = new ArrayList<>();
    public ArrayList<UserListener> user = new ArrayList<>();
    public final List<SystemHelperListener> systemHelper = new ArrayList<>();

}
