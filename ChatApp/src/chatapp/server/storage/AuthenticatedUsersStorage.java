package chatapp.server.storage;

import chatapp.server.models.AuthUser;

import java.util.HashMap;
import java.util.HexFormat;

public class AuthenticatedUsersStorage extends HashMap<String, AuthUser> {
    public AuthenticatedUsersStorage() {
        byte[] salt = HexFormat.of().parseHex("5d51e2d14ed9c0abc74e905c02b25c1f");
        byte[] hash = HexFormat.of().parseHex("160c238863a65d773155053b778952e7"); // authUser1
        this.put("authUser1",new AuthUser("authUser1",hash,salt,null));
    }
}
