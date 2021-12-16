package chatapp.shared.models;

import chatapp.shared.Globals;

public class AuthenticatedUser extends User{
    private byte[] hash;

    public AuthenticatedUser(String name,String password ,Globals globals) {
        super(name, globals);
    }

    public boolean validate(String password) {
        return false;
    }
}
