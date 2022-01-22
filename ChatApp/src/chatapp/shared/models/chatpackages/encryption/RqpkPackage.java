package chatapp.shared.models.chatpackages.encryption;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.Base64;

public class RqpkPackage extends ChatPackage {
    private String user;
    private byte[] key;

    public RqpkPackage(String user,byte[] key) {
        this.type = ChatPackageType.RQPK;
        this.user = user;
        this.key = key;
    }

    public RqpkPackage(String user) {
        this(user,null);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public boolean isSet() {
        return key != null;
    }

    public static RqpkPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2);
        if (packageParts == null) return null;
        var dec = Base64.getDecoder();
        byte[] key = packageParts.length >= 3 ? dec.decode(packageParts[2]) : null;

        return new RqpkPackage(packageParts[1], key);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(type + " " + user);
        var enc = Base64.getEncoder();
        if (key != null) stringBuilder.append(" ").append(enc.encodeToString(key));
        return stringBuilder.toString();
    }
}
