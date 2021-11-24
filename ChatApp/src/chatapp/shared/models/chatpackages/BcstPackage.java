package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class BcstPackage extends ChatPackage {

    private String sender;
    private String groupName;
    private String message;


    public BcstPackage(String groupName, String message) {
        this(null, groupName, message);
    }

    public BcstPackage(String sender, String groupName, String message) {
        this.sender = sender;
        this.groupName = groupName;
        this.message = message;

        this.type = ChatPackageType.BCST;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getMessage() {
        return message;
    }


    public static BcstPackage deserializeClient(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 3, packageParts.length));
        return new BcstPackage(packageParts[1], packageParts[2], message);
    }

    public static BcstPackage deserializeServer(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 2, packageParts.length));
        return new BcstPackage(packageParts[1], message);
    }

    @Override
    public String toString() {
        return  type + " " +
                (sender != null ? sender + " " : "") +
                groupName + " " +
                message;
    }

}
