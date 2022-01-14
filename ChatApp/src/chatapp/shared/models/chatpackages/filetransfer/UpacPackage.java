package chatapp.shared.models.chatpackages.filetransfer;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;

public class UpacPackage extends ChatPackage {
    private String user;

    public UpacPackage(String user) {
        this.type = ChatPackageType.UPAC;
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public static UpacPackage deserialize(String packageStr) {
        String[] strings = splitPackageStr(packageStr,2,true);
        if (strings == null) return null;
        return new UpacPackage(strings[1]);
    }

    @Override
    public String toString() {
        return type + " " + user;
    }
}
