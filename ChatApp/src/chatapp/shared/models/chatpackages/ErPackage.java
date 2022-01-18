package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class ErPackage extends ChatPackage {

    public static final ErPackage UNKNOWN = new ErPackage(0, "Unknown command.");
    public static final ErPackage ALREADY_LOGGED_IN = new ErPackage(1, "User already logged in.");
    public static final ErPackage USER_NAME_INVALID = new ErPackage(2, "Username has an invalid format " +
            "(only characters, numbers and underscores are allowed).");
    public static final ErPackage NOT_LOGGED_IN = new ErPackage(3, "Please log in first.");
    public static final ErPackage GROUP_NAME_INVALID = new ErPackage(4, "Group name has an invalid format " +
            "(only characters, numbers and underscores are allowed).");
    public static final ErPackage NOT_IN_GROUP = new ErPackage(15, "You are not in the group.");
    public static final ErPackage USER_NAME_EXISTS = new ErPackage(24, "Username already exists.");
    public static final ErPackage LOG_IN_INVALID = new ErPackage(25, "Username or Password is incorrect.");
    public static final ErPackage PACKAGE_INVALID = new ErPackage(30, "Invalid (number of) values.");
    public static final ErPackage FILE_TRANSFER_INCORRECT = new ErPackage(51, "Invalid file transfer.");


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
