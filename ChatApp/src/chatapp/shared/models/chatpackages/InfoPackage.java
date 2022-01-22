package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class InfoPackage extends ChatPackage {

    private final String message;


    public InfoPackage(String message) {
        this.message = message;

        this.type = ChatPackageType.INFO;
    }


    public String getMessage() {
        return message;
    }


    public static InfoPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2);
        if (packageParts == null) return null;

        String message = String.join(" ", Arrays.copyOfRange(packageParts, 1, packageParts.length));
        return new InfoPackage(message);
    }

    @Override
    public String toString() {
        return type + " " +
                message;
    }

}
