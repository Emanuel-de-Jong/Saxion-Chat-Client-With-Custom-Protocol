package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class BcstPackage extends ChatPackage {

    private String sender;
    private String message;


    public BcstPackage(String message) {
        this.message = message;

        this.type = ChatPackageType.BCST;
    }

    public BcstPackage(String sender, String message) {
        this(message);
        this.sender = sender;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static BcstPackage deserializeClient(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 3);
        if (packageParts == null) return null;

        String message = String.join(" ", Arrays.copyOfRange(packageParts, 2, packageParts.length));
        return new BcstPackage(packageParts[1], message);
    }

    public static BcstPackage deserializeServer(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2);
        if (packageParts == null) return null;

        String message = String.join(" ", Arrays.copyOfRange(packageParts, 1, packageParts.length));
        return new BcstPackage(message);
    }

    @Override
    public String toString() {
        return  type + " " +
                (sender != null ? sender + " " : "") +
                message;
    }

}
