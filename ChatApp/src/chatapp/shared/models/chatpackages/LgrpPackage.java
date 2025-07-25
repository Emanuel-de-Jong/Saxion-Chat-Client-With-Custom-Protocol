package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class LgrpPackage extends ChatPackage {

    private final String groupName;
    private String userName;


    public LgrpPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.LGRP;
    }

    public LgrpPackage(String groupName, String userName) {
        this(groupName);
        this.userName = userName;
    }


    public String getGroupName() {
        return groupName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public static LgrpPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2, 3);
        if (packageParts == null) return null;

        LgrpPackage result = new LgrpPackage(packageParts[1]);
        if (packageParts.length > 2) {
            result.setUserName(packageParts[2]);
        }

        return result;
    }

    @Override
    public String toString() {
        return type + " " +
                groupName +
                (userName != null ? " " + userName : "");
    }

}
