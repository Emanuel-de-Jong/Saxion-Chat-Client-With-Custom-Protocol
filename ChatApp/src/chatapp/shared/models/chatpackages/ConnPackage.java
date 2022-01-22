package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class ConnPackage extends ChatPackage {

    private final String userName;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasPassword() {
        return password != null;
    }


    public static ConnPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 1, 3);
        if (packageParts == null) return null;

        ConnPackage connPackage;
        switch (packageParts.length) {
            case 2 -> connPackage = new ConnPackage(packageParts[1]);
            case 3 -> connPackage = new ConnPackage(packageParts[1], packageParts[2]);
            default -> connPackage = new ConnPackage("");
        }

        return connPackage;
    }

    @Override
    public String toString() {
        return type + " " +
                userName +
                (password != null ? " " + password : "");
    }

}
