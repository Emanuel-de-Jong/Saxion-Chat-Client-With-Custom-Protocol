package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class DscnPackage extends ChatPackage {

    public DscnPackage() {
        this.type = ChatPackageType.DSCN;
    }


    public static DscnPackage deserialize(String packageStr) {
        return new DscnPackage();
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
