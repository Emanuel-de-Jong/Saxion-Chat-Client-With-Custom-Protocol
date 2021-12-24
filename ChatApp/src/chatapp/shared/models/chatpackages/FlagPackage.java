package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.enums.Flag;

public class FlagPackage extends ChatPackage {

    private final Flag flag;


    public FlagPackage(Flag flag) {
        this.flag = flag;

        this.type = ChatPackageType.FLAG;
    }


    public Flag getFlag() {
        return flag;
    }


    public static FlagPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2, true);
        if (packageParts == null) return null;

        return new FlagPackage(Flag.valueOf(packageParts[1]));
    }

    @Override
    public String toString() {
        return  type + " " +
                flag;
    }

}
