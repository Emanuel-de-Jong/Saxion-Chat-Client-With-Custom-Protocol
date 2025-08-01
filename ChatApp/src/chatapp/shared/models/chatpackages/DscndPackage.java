package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class DscndPackage extends ChatPackage {

    private final String userName;


    public DscndPackage(String userName) {
        this.userName = userName;

        this.type = ChatPackageType.DSCND;
    }


    public String getUserName() {
        return userName;
    }


    public static DscndPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2, true);
        if (packageParts == null) return null;

        return new DscndPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return type + " " +
                userName;
    }

}
