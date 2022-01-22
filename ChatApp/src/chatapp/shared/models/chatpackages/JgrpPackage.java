package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class JgrpPackage extends ChatPackage {

    private final String groupName;
    private String userName;


    public JgrpPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.JGRP;
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


    public static JgrpPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2, 3);
        if (packageParts == null) return null;

        JgrpPackage result = new JgrpPackage(packageParts[1]);
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
