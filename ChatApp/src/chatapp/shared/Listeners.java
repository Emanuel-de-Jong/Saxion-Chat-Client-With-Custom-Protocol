package chatapp.shared;

import chatapp.shared.interfaces.GroupListener;
import chatapp.shared.interfaces.UserListener;

import java.util.ArrayList;

public class Listeners {

    public ArrayList<GroupListener> group = new ArrayList<>();
    public ArrayList<UserListener> user = new ArrayList<>();

}
