package chatapp.client.interfaces;

import chatapp.shared.models.Group;

public interface AddGroupDialogListener {

    void groupJoined(Group group);

    void createGroup(String name);

}
