package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class GrpPackage extends ChatPackage {

    private final String groupName;


    public GrpPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.GRP;
    }


    public String getGroupName() {
        return groupName;
    }


    public static GrpPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2, true);
        if (packageParts == null) return null;

        return new GrpPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return  type + " " +
                groupName;
    }

}
