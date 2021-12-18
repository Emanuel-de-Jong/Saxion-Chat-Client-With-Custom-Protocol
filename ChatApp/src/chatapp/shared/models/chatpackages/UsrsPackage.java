package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class UsrsPackage extends ChatPackage {

    private final HashMap<String,Boolean> userNames = new HashMap<>();


    public UsrsPackage() {
        this.type = ChatPackageType.USRS;
    }


    public String[] getUserNames() {
        return userNames.keySet().toArray(new String[0]);
    }

    public void addUserName(String userName, boolean verified) {
        userNames.putIfAbsent(userName,verified);
    }

    public boolean isVerified(String userName) {
        return userNames.get(userName);
    }


    public static UsrsPackage deserialize(String packageStr) {
        String[] packageParts = packageStr.split(" ");

        UsrsPackage result = new UsrsPackage();
        for (int i = 1; i < packageParts.length; i++) {
            String name = packageParts[i];
            boolean verified = false;
            if (Pattern.matches("^\\*\\S+",name)) {
                name = name.substring(1);
                verified = true;
            }
            result.addUserName(name,verified);
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(type.toString());
        userNames.forEach((name,verified) -> {
            out.append(" ");
            if (verified) out.append("*");
            out.append(name);
        });
        return out.toString();
    }

}
