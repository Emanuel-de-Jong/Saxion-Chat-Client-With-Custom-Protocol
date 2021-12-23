package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class CgrpPackage extends ChatPackage {

    private final String groupName;


    public CgrpPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.CGRP;
    }


    public String getGroupName() {
        return groupName;
    }


    public static CgrpPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2, true);
        if (packageParts == null) return null;

        return new CgrpPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return  type + " " +
                groupName;
    }

}
