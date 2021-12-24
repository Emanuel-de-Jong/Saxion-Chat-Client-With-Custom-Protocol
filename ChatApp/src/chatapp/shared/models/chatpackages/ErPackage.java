package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class ErPackage extends ChatPackage {

    public static final ErPackage unknown = new ErPackage(0, "Unknown command");
    public static final ErPackage alreadyLoggedIn = new ErPackage(1, "User already logged in");
    public static final ErPackage userNameInvalid = new ErPackage(2, "Username has an invalid format " +
            "(only characters, numbers and underscores are allowed)");
    public static final ErPackage notLoggedIn = new ErPackage(3, "Please log in first");
    public static final ErPackage groupNameInvalid = new ErPackage(4, "Group name has an invalid format " +
            "(only characters, numbers and underscores are allowed)");
    public static final ErPackage notInGroup = new ErPackage(15, "You are not in the group");
    public static final ErPackage userNameExists = new ErPackage(24, "Username already exists");
    public static final ErPackage logInInvalid = new ErPackage(25, "Username or Password is incorrect");
    public static final ErPackage packageInvalid = new ErPackage(30, "Invalid (number of) values");


    private final int code;
    private final String message;


    public ErPackage(int code, String message) {
        this.code = code;
        this.message = message;

        this.type = ChatPackageType.ER;
    }


    public int getCode() {
        return code;
    }

    public String getCodeString() {
        return String.format("%02d", code);
    }

    public String getMessage() {
        return message;
    }


    public static ErPackage deserialize(String packageStr) {
        String[] packageParts = splitPackageStr(packageStr, 2);
        if (packageParts == null) return null;

        int code = Integer.parseInt(packageParts[0].replaceAll("[^0-9]", ""));
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 1, packageParts.length));
        return new ErPackage(code, message);
    }

    @Override
    public String toString() {
        return  type.toString() +
                getCodeString() + " " +
                message;
    }

}
