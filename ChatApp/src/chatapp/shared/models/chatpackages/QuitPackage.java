package chatapp.shared.models.chatpackages;

import chatapp.shared.enums.ChatPackageType;

public class QuitPackage extends ChatPackage {

    public QuitPackage() {
        this.type = ChatPackageType.QUIT;
    }


    public static QuitPackage deserialize() {
        return new QuitPackage();
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
