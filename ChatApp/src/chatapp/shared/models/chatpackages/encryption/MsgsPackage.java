package chatapp.shared.models.chatpackages.encryption;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.Base64;

public class MsgsPackage extends ChatPackage {
    private String sender;
    private final String receiver;
    private final byte[] encryptedMessage;


    public MsgsPackage(String receiver, byte[] encryptedMessage) {
        this(null, receiver, encryptedMessage);
    }

    public MsgsPackage(String sender, String receiver, byte[] encryptedMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.encryptedMessage = encryptedMessage;

        this.type = ChatPackageType.MSGS;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public byte[] getMessage() {
        return encryptedMessage;
    }


    public static MsgsPackage deserializeClient(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 4);
        if (packageParts == null) return null;

        var dec = Base64.getDecoder();
        return new MsgsPackage(packageParts[1], packageParts[2], dec.decode(packageParts[3]));
    }

    public static MsgsPackage deserializeServer(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 3);
        if (packageParts == null) return null;

        var dec = Base64.getDecoder();
        return new MsgsPackage(packageParts[1], dec.decode(packageParts[2]));
    }

    @Override
    public String toString() {
        var enc = Base64.getEncoder();
        return type + " " +
                (sender != null ? sender + " " : "") +
                receiver + " " +
                enc.encodeToString(encryptedMessage);
    }
}
