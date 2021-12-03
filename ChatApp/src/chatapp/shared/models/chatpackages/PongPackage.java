package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class PongPackage extends ChatPackage {

    public PongPackage() {
        this.type = ChatPackageType.PONG;
    }


    public static PongPackage deserialize() {
        return new PongPackage();
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
