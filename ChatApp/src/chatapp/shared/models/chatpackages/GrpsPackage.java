package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class GrpsPackage extends ChatPackage {

    private String[] groupNames = new String[0];


    public GrpsPackage() {
        this.type = ChatPackageType.GRPS;
    }


    public String[] getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String[] groupNames) {
        this.groupNames = groupNames;
    }


    public static GrpsPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 1);
        if (packageParts == null) return null;

        GrpsPackage result = new GrpsPackage();
        if (packageParts.length > 1) {
            result.setGroupNames(Arrays.copyOfRange(packageParts, 1, packageParts.length));
        }

        return result;
    }

    @Override
    public String toString() {
        return type +
                (groupNames != null ? " " + String.join(" ", groupNames) : "");
    }

}
