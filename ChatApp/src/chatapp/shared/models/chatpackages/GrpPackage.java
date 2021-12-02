package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class GrpPackage extends ChatPackage {

    private String groupName;


    public GrpPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.GRP;
    }


    public String getGroupName() {
        return groupName;
    }


    public static GrpPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        return new GrpPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return  type + " " +
                groupName;
    }

}
