package chatapp.shared.models;

import chatapp.shared.Globals;

public class AuthenticatedUser extends User {

    private byte[] hash;
    private String password;


    public AuthenticatedUser(String name, String password, Globals globals) {
        super(name, globals);
        this.password = password;
    }


    public String getPassword() {
        return password;
    }


    public boolean validate(String password) {
        return false;
    }
}
