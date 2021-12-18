package chatapp.server.storage;

import chatapp.server.models.AuthUser;

import java.util.HashMap;
import java.util.HexFormat;

public class AuthUsersStorage extends HashMap<String, AuthUser> {
    public AuthUsersStorage() {
        byte[] salt = HexFormat.of().parseHex("f7a4779c3a7e213d6eece69c4d18b861");
        byte[] hash = HexFormat.of().parseHex("a8f56e8f747edea70438c88effc2ad6b"); // authUser1
        this.put("authUser1",new AuthUser("authUser1",hash,salt));
        salt = HexFormat.of().parseHex("3e7b7601674081912dfab5e2cac80648");
        hash = HexFormat.of().parseHex("871a4b04172e3257ea01828924d14bcd");
        this.put("authUser2",new AuthUser("authUser2",hash,salt));
        salt = HexFormat.of().parseHex("6f5a2da6eef75bb7586ad8db048d8c75");
        hash = HexFormat.of().parseHex("63b8b557bb85cea4792fbeba68145f03");
        this.put("authUser3",new AuthUser("authUser3",hash,salt));
    }

    @Override
    public AuthUser get(Object key) {
        var out = super.get(key);
        if (out == null) return new AuthUser(null,null,null);
        return out;
    }
}
