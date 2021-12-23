package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class GbcstPackage extends ChatPackage {

    private String sender;
    private String groupName;
    private String message;


    public GbcstPackage(String groupName, String message) {
        this(null, groupName, message);
    }

    public GbcstPackage(String sender, String groupName, String message) {
        this.sender = sender;
        this.groupName = groupName;
        this.message = message;

        this.type = ChatPackageType.GBCST;
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

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static GbcstPackage deserializeClient(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 3, packageParts.length));
        return new GbcstPackage(packageParts[1], packageParts[2], message);
    }

    public static GbcstPackage deserializeServer(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 2, packageParts.length));
        return new GbcstPackage(packageParts[1], message);
    }

    @Override
    public String toString() {
        return  type + " " +
                (sender != null ? sender + " " : "") +
                groupName + " " +
                message;
    }

}
