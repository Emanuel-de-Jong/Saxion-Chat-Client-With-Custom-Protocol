package chatapp.shared.models.chatpackages.filetransfer;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.Base64;

public class DnacPackage extends ChatPackage {
    private final String user;
    private final byte[] hash;
    private final byte[] connection;

    public DnacPackage(String user, byte[] hash, byte[] connection) {
        this.type = ChatPackageType.DNAC;
        this.user = user;
        this.hash = hash;
        this.connection = connection;
    }

    public static ChatPackage deserialize(String packageStr) {
        var dc = Base64.getDecoder();
        String[] strings = splitPackageStr(packageStr, 4, true);
        if (strings == null) return null;
        return new DnacPackage(
                strings[1],
                dc.decode(strings[2]),
                dc.decode(strings[3])
        );
    }

    public String getUser() {
        return user;
    }

    public byte[] getConnection() {
        return connection;
    }

    public byte[] getHash() {
        return hash;
    }

    @Override
    public String toString() {
        var enc = Base64.getEncoder();
        return type + " " + user + " " + enc.encodeToString(hash) + " " + enc.encodeToString(connection);
    }


}
