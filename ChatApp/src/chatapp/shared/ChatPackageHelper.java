package chatapp.shared;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.*;

public class ChatPackageHelper {

    public static ChatPackage deserialize(String packageStr, boolean isClient) {
        String cmd = packageStr.split(" ")[0].replaceAll("\\d", "");
        ChatPackageType type = ChatPackageType.valueOf(cmd);

        ChatPackage chatPackage = null;
        switch (type) {
            case BCST:
                chatPackage = isClient ?
                        BcstPackage.deserializeClient(packageStr) :
                        BcstPackage.deserializeServer(packageStr);
                break;
            case CONN:
                chatPackage = ConnPackage.deserialize(packageStr);
                break;
            case DSCN:
                chatPackage = DscnPackage.deserialize(packageStr);
                break;
            case ER:
                chatPackage = ErPackage.deserialize(packageStr);
                break;
            case INFO:
                chatPackage = InfoPackage.deserialize(packageStr);
                break;
            case CGRP:
                chatPackage = CgrpPackage.deserialize(packageStr);
                break;
            case JGRP:
                chatPackage = JgrpPackage.deserialize(packageStr);
                break;
            case LGRP:
                chatPackage = LgrpPackage.deserialize(packageStr);
                break;
            case MSG:
                chatPackage = isClient ?
                        MsgPackage.deserializeClient(packageStr) :
                        MsgPackage.deserializeServer(packageStr);
                break;
            case OK:
                chatPackage = OkPackage.deserialize(packageStr);
                break;
            case PING:
                chatPackage = PingPackage.deserialize();
                break;
            case PONG:
                chatPackage = PongPackage.deserialize();
                break;
            case QUIT:
                chatPackage = QuitPackage.deserialize();
                break;
            case USR:
                chatPackage = UsrPackage.deserialize(packageStr);
                break;
            case USRS:
                chatPackage = UsrsPackage.deserialize(packageStr);
                break;
            case GRP:
                chatPackage = GrpPackage.deserialize(packageStr);
                break;
            case GRPS:
                chatPackage = GrpsPackage.deserialize(packageStr);
                break;
        }

        return chatPackage;
    }

}
