package chatapp.server.data;

import chatapp.server.clientthreads.filetransfer.FileTransferHandler;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class FileTransferHandlers extends HashMap<Long, FileTransferHandler> {

    public FileTransferHandler get(byte[] byteArray) {
        if (byteArray.length != 8) throw new IllegalArgumentException("FileTransferHandler key has to contain 8 bytes");
        return super.get(ByteBuffer.wrap(byteArray).getLong());
    }

    public FileTransferHandler put(byte[] byteArray, FileTransferHandler value) {
        if (byteArray.length != 8) throw new IllegalArgumentException("FileTransferHandler key has to contain 8 bytes");
        return super.put(ByteBuffer.wrap(byteArray).getLong(), value);
    }
}
