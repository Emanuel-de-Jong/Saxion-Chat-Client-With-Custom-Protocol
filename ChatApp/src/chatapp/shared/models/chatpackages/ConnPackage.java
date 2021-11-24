package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class ConnPackage extends ChatPackage {

    private String userName;
    private String password;


    public ConnPackage(String userName) {
        this(userName, null);
    }

    public ConnPackage(String userName, String password) {
        this.userName = userName;
        this.password = password;

        this.type = ChatPackageType.CONN;
    }


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }


    public static ConnPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");

        ConnPackage connPackage = null;
        if (packageParts.length == 3) {
            connPackage = new ConnPackage(packageParts[1], packageParts[2]);
        } else {
            connPackage = new ConnPackage(packageParts[1]);
        }

        return connPackage;
    }

    @Override
    public String toString() {
        return  type + " " +
                userName +
                (password != null ? " " + password : "");
    }

}
