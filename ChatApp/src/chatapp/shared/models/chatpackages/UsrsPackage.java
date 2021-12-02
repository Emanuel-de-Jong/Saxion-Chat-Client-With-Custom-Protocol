package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class UsrsPackage extends ChatPackage {

    private String[] userNames;


    public UsrsPackage() {
        this.type = ChatPackageType.USRS;
    }


    public String[] getUserNames() {
        return userNames;
    }

    public void setUserNames(String[] userNames) {
        this.userNames = userNames;
    }


    public static UsrsPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");

        UsrsPackage result = new UsrsPackage();
        if (packageParts.length > 1) {
            result.setUserNames(Arrays.copyOfRange(packageParts, 1, packageParts.length));
        }

        return result;
    }

    @Override
    public String toString() {
        return  type +
                (userNames != null ? " " + String.join(" ", userNames) : "");
    }

}
