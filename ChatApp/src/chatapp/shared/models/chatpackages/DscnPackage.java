package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class DscnPackage extends ChatPackage {

    private final String message;


    public DscnPackage(String message) {
        this.message = message;

        this.type = ChatPackageType.DSCN;
    }


    public String getMessage() {
        return message;
    }


    public static DscnPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 1, packageParts.length));
        return new DscnPackage(message);
    }

    @Override
    public String toString() {
        return  type + " " +
                message;
    }

}
