package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class JgrpPackage extends ChatPackage {

    private String groupName;


    public JgrpPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.JGRP;
    }


    public String getGroupName() {
        return groupName;
    }


    public static JgrpPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        return new JgrpPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return  type + " " +
                groupName;
    }

}
