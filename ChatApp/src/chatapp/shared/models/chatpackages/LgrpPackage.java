package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class LgrpPackage extends ChatPackage {

    private String groupName;


    public LgrpPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.LGRP;
    }


    public String getGroupName() {
        return groupName;
    }


    public static LgrpPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        return new LgrpPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return  type + " " +
                groupName;
    }

}
