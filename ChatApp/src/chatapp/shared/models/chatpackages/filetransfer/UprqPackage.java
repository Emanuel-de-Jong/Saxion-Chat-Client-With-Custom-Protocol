package chatapp.shared.models.chatpackages.filetransfer;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.User;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.Arrays;
import java.util.Base64;

public class UprqPackage extends ChatPackage {

    private byte[] connection;
    private byte[] hash;
    private String user;
    private String fileName;
    private int fileSize;

    public UprqPackage(String user, String fileName, int fileSize, byte[] hash, byte[] connection) {
        super();
        this.type = ChatPackageType.UPRQ;
        this.connection = connection;
        this.hash = hash;
        this.user = user;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public static ChatPackage deserialize(String packageStr) {
        var decoder = Base64.getDecoder();
        String[] strings = splitPackageStr(packageStr,6,true);
        return new UprqPackage(
                strings[1],
                strings[2],
                Integer.parseInt(strings[3]),
                decoder.decode(strings[4]),
                decoder.decode(strings[5])
        );
    }

    @Override
    public String toString() {
        var encoder = Base64.getEncoder();
        return type + " " + user + " " + fileName + " " + fileSize + " " + encoder.encodeToString(hash) + " " + encoder.encodeToString(connection);
    }
}

