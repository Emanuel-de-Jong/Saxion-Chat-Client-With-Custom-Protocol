package chatapp.shared;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.*;
import chatapp.shared.models.chatpackages.filetransfer.DnrqPackage;
import chatapp.shared.models.chatpackages.filetransfer.UprqPackage;

public class ChatPackageHelper {

    public static ChatPackage deserialize(String packageStr, boolean isClient) throws IllegalArgumentException {
        String cmd = packageStr.split(" ")[0].replaceAll("\\d", "");
        ChatPackageType type = ChatPackageType.valueOf(cmd);

        ChatPackage chatPackage = null;
        switch (type) {
            case OK -> chatPackage = OkPackage.deserialize(packageStr);
            case INFO -> chatPackage = InfoPackage.deserialize(packageStr);
            case ER -> chatPackage = ErPackage.deserialize(packageStr);
            case CONN -> chatPackage = ConnPackage.deserialize(packageStr);
            case FLAG -> chatPackage = FlagPackage.deserialize(packageStr);
            case USR -> chatPackage = UsrPackage.deserialize(packageStr);
            case USRS -> chatPackage = UsrsPackage.deserialize(packageStr);
            case GRP -> chatPackage = GrpPackage.deserialize(packageStr);
            case GRPS -> chatPackage = GrpsPackage.deserialize(packageStr);
            case CGRP -> chatPackage = CgrpPackage.deserialize(packageStr);
            case JGRP -> chatPackage = JgrpPackage.deserialize(packageStr);
            case LGRP -> chatPackage = LgrpPackage.deserialize(packageStr);
            case GTMT -> chatPackage = GtmtPackage.deserialize(packageStr);
            case MSG -> chatPackage = isClient ?
                    MsgPackage.deserializeClient(packageStr) :
                    MsgPackage.deserializeServer(packageStr);
            case BCST -> chatPackage = isClient ?
                    BcstPackage.deserializeClient(packageStr) :
                    BcstPackage.deserializeServer(packageStr);
            case GBCST -> chatPackage = isClient ?
                    GbcstPackage.deserializeClient(packageStr) :
                    GbcstPackage.deserializeServer(packageStr);
            case PING -> chatPackage = PingPackage.deserialize();
            case PONG -> chatPackage = PongPackage.deserialize();
            case QUIT -> chatPackage = QuitPackage.deserialize();
            case DSCN -> chatPackage = DscnPackage.deserialize(packageStr);
            case DSCND -> chatPackage = DscndPackage.deserialize(packageStr);
            case UPRQ -> chatPackage = UprqPackage.deserialize(packageStr);
            case DNRQ -> chatPackage = DnrqPackage.deserialize(packageStr);
        }
        return chatPackage;
    }

}
