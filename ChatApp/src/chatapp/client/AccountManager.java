package chatapp.client;

import chatapp.client.models.User;

public class AccountManager {

    private User user;
    private boolean loggedIn = false;


    public AccountManager(User user) {
        this.user = user;
    }


    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

}
