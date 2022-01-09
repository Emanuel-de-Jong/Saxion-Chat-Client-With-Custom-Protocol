package chatapp.shared.models.chatpackages.filetransfer;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.Arrays;
import java.util.Base64;

public class UprqPackage extends ChatPackage {

    byte[] connection;
    byte[] hash;
    User user;

    public UprqPackage(byte[] connection, byte[] hash, User user) {
        super();
        this.type = ChatPackageType.UPRQ;
        this.connection = connection;
        this.hash = hash;
        this.user = user;
    }

    @Override
    public String toString() {
        var encoder = Base64.getEncoder();
        return type + " " + user + " " + encoder.encodeToString(connection) + " " + encoder.encodeToString(hash);
    }
}

