package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;

public class ErPackage extends ChatPackage {

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

    public String getMessage() {
        return message;
    }


    public static ErPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");
        int code = Integer.parseInt(packageParts[0].replaceAll("[^0-9]", ""));
        String message = String.join(" ", Arrays.copyOfRange(packageParts, 1, packageParts.length));
        return new ErPackage(code, message);
    }

    @Override
    public String toString() {
        return  type.toString() +
                String.format("%02d", code) + " " +
                message;
    }

}
