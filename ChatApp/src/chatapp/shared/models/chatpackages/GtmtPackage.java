package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class GtmtPackage extends ChatPackage {

    private final String groupName;


    public GtmtPackage(String groupName) {
        this.groupName = groupName;

        this.type = ChatPackageType.GTMT;
    }


    public String getGroupName() {
        return groupName;
    }


    public static GtmtPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        return new GtmtPackage(packageParts[1]);
    }

    @Override
    public String toString() {
        return  type + " " +
                groupName;
    }

}
