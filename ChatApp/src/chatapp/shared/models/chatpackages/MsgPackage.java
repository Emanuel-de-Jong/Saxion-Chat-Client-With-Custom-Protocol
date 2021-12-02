package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class MsgPackage extends ChatPackage {

    private String sender;
    private String receiver;
    private String message;


    public MsgPackage(String receiver, String message) {
        this(null, receiver, message);
    }

    public MsgPackage(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;

        this.type = ChatPackageType.MSG;
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

    public String getMessage() {
        return message;
    }


    public static MsgPackage deserializeClient(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 3, packageParts.length));
        return new MsgPackage(packageParts[1], packageParts[2], message);
    }

    public static MsgPackage deserializeServer(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 2, packageParts.length));
        return new MsgPackage(packageParts[1], message);
    }

    @Override
    public String toString() {
        return  type + " " +
                (sender != null ? sender + " " : "") +
                receiver + " " +
                message;
    }

}
