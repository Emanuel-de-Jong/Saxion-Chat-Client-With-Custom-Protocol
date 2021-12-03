package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class PingPackage extends ChatPackage {

    public PingPackage() {
        this.type = ChatPackageType.PING;
    }


    public static PingPackage deserialize() {
        return new PingPackage();
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
