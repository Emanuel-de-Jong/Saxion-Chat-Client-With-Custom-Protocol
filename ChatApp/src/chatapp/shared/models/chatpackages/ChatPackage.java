package chatapp.shared.models.chatpackages;

import chatapp.shared.SystemHelper;
import chatapp.shared.enums.ChatPackageType;

public class ChatPackage {

    protected ChatPackageType type;

    public ChatPackageType getType() {
        return type;
    }

    public static String[] splitPackageStr(String packageStr, int min) {
        return splitPackageStr(packageStr, min, false);
    }

    public static String[] splitPackageStr(String packageStr, int min, boolean minIsMax) {
        String[] packageParts = packageStr.split(" ");
        if (packageParts.length < min) return null;
        if (minIsMax && packageParts.length > min) return null;
        return packageParts;
    }

    public static String[] splitPackageStr(String packageStr, int min, int max) {
        String[] packageParts = splitPackageStr(packageStr, min);
        if (packageParts == null || packageParts.length > max) return null;
        return packageParts;
    }

}
