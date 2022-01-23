package chatapp.server.data;

import chatapp.server.models.AuthUser;

import java.util.HashMap;
import java.util.HexFormat;

public class AuthUsersStorage extends HashMap<String, AuthUser> {
    public AuthUsersStorage() {
        byte[] salt = HexFormat.of().parseHex("6145beea8500bf8ea22baa5b470f7ab8");
        byte[] hash = HexFormat.of().parseHex("bb2e128d816a1919df98c387fe17ea0c"); // authUser1
        this.put("authUser1", new AuthUser("authUser1", hash, salt));
        salt = HexFormat.of().parseHex("41f9d0846497e5d726c905f52cab3a69");
        hash = HexFormat.of().parseHex("166470a4048119dee100817e2e76db26"); // authUser2
        this.put("authUser2", new AuthUser("authUser2", hash, salt));
        salt = HexFormat.of().parseHex("cb2f4ed76e787cef79d79eb61ffbae33");
        hash = HexFormat.of().parseHex("fbb16aca01ec2f0f616cc6b6e2b71a99"); //authUser3
        this.put("authUser3", new AuthUser("authUser3", hash, salt));
        salt = HexFormat.of().parseHex("4d852983d4053bb1ef74bfeb51bd3856");
        hash = HexFormat.of().parseHex("d094043e60dd7d3951a59680fb3fdb28"); //peter123
        this.put("Peter", new AuthUser("Peter", hash, salt));
        salt = HexFormat.of().parseHex("f3082a8bb9bdedc007f062decbf945c6");
        hash = HexFormat.of().parseHex("62ca9c030903f166b238cdb86bad442e"); //manuel123
        this.put("Manuel", new AuthUser("Manuel", hash, salt));
    }

    @Override
    public AuthUser get(Object key) {
        var out = super.get(key);
        if (out == null) return new AuthUser(null, null, null);
        return out;
    }
}
