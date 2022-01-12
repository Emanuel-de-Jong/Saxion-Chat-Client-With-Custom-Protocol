package chatapp.shared.models.chatpackages.filetransfer;

import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;

import java.util.Base64;

public class DnrqPackage extends ChatPackage {
    private byte[] hash;
    private String user;
    private String fileName;
    private int fileSize;

    public DnrqPackage(String user, String fileName, int fileSize, byte[] hash) {
        super();
        this.type = ChatPackageType.DNRQ;
        this.hash = hash;
        this.user = user;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public static ChatPackage deserialize(String packageStr) {
        var decoder = Base64.getDecoder();
        String[] strings = splitPackageStr(packageStr,5,true);
        return new DnrqPackage(
                strings[1],
                strings[2],
                Integer.parseInt(strings[3]),
                decoder.decode(strings[4])
        );
    }

    public byte[] getHash() {
        return hash;
    }

    public String getUser() {
        return user;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        var encoder = Base64.getEncoder();
        return type + " " + user + " " + fileName + " " + fileSize + " " + encoder.encodeToString(hash);
    }
}
