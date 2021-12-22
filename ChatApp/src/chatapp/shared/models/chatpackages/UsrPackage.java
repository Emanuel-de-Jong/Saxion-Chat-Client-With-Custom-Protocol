package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

import java.util.regex.Pattern;

public class UsrPackage extends ChatPackage {

    private final String userName;
    private final boolean verified;


    public UsrPackage(String userName, boolean verified) {
        this.userName = userName;
        this.verified = verified;
        this.type = ChatPackageType.USR;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getUserName() {
        return userName;
    }


    public static UsrPackage deserialize(String packageStr) {
        String userName = packageStr.split(" ")[1];
        boolean verified = false;
        if (Pattern.matches("^\\*\\S+", userName)) {
            userName = userName.substring(1);
            verified = true;
        }
        return new UsrPackage(userName, verified);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(type.toString());
        out.append(" ");
        if (verified) out.append("*");
        out.append(userName);
        return out.toString();
    }

}
