package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;

public interface LogInDialogListener extends Listener {

    void logInDialogClosed(String name, String username, String password);

}
