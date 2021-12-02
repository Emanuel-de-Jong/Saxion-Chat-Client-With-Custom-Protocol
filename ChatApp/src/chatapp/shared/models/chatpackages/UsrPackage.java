package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class UsrPackage extends ChatPackage {

    private String userName;


    public UsrPackage(String userName) {
        this.userName = userName;

        this.type = ChatPackageType.USR;
    }


    public String getUserName() {
        return userName;
    }


    public static UsrPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        return new UsrPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return  type + " " +
                userName;
    }

}
