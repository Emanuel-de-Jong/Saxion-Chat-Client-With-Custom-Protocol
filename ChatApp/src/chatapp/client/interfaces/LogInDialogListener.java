package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;

public interface LogInDialogListener extends Listener {

    void logIn(String username, String password);

    void logInDialogClosed(String name, String username, String password);

}
