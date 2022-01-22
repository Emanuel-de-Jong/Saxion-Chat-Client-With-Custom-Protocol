package chatapp.shared.models.chatpackages.encryption;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.Base64;

public class SeskPackage extends ChatPackage {
    private String user;
    private byte[] key;
    private byte[] initializationVector;

    public SeskPackage(String user, byte[] key, byte[] initializationVector) {
        this.type = ChatPackageType.SESK;
        this.user = user;
        this.key = key;
        this.initializationVector = initializationVector;
    }

    public SeskPackage(String user) {
        this(user, null, null);
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

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(byte[] initializationVector) {
        this.initializationVector = initializationVector;
    }

    public boolean isSet() {
        return key != null && initializationVector != null;
    }

    public static SeskPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2);
        if (packageParts == null) return null;
        var dec = Base64.getDecoder();
        byte[] key = packageParts.length >= 4 ? dec.decode(packageParts[2]) : null;
        byte[] iv = packageParts.length >= 4 ? dec.decode(packageParts[3]) : null;
        return new SeskPackage(packageParts[1], key, iv);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(type + " " + user);
        var enc = Base64.getEncoder();
        if (key != null) stringBuilder.append(" ").append(enc.encodeToString(key));
        if (initializationVector != null) stringBuilder.append(" ").append(enc.encodeToString(initializationVector));
        return stringBuilder.toString();
    }
}