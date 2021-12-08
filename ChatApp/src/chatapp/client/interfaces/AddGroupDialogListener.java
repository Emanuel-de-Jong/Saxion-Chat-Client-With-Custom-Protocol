package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;

public interface AddGroupDialogListener extends Listener {

    void createGroup(String name);

}
