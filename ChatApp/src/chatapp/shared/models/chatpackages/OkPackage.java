package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class OkPackage extends ChatPackage {

    private final String message;


    public OkPackage(String message) {
        this.message = message;

        this.type = ChatPackageType.OK;
    }


    public String getMessage() {
        return message;
    }


    public static OkPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 1, packageParts.length));
        return new OkPackage(message);
    }

    @Override
    public String toString() {
        return  type + " " +
                message;
    }
}
