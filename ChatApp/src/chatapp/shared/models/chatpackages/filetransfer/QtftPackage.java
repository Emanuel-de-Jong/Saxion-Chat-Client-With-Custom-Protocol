package chatapp.shared.models.chatpackages.filetransfer;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;

public class QtftPackage extends ChatPackage {
    private String user;

    public QtftPackage(String user) {
        this.type = ChatPackageType.QTFT;
        this.user = user;
    }

    public static QtftPackage deserialize(String packageStr) {
        String[] strings = splitPackageStr(packageStr,2,true);
        if (strings == null) return null;
        return new QtftPackage(strings[1]);
    }

    @Override
    public String toString() {
        return type + " " + user;
    }
}
