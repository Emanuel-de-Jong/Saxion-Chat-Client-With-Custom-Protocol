package chatapp.shared;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.*;

public class ChatPackageHelper {

    public static ChatPackage deserializeClient(String packageStr) {
        String cmd = packageStr.split(" ")[0].replaceAll("\\d", "");
        ChatPackageType type = ChatPackageType.valueOf(cmd);

        ChatPackage chatPackage = null;
        switch (type) {
            case BCST:
                chatPackage = BcstPackage.deserializeClient(packageStr);
        }

        return chatPackage;
    }

}
