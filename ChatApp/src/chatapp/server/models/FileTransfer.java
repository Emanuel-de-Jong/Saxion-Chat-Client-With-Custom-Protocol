package chatapp.server.models;

import chatapp.server.clientthreads.FileTransferHandler;
import chatapp.shared.models.User;

public class FileTransfer {
    private final User sender;
    private final User receiver;

    private final FileTransferHandler senderFileTransferHandler;
    private FileTransferHandler receiverFileTransferHandler;

    private final String filename;
    private final byte[] hash;
    private final int filesize;

    public FileTransfer(User sender, FileTransferHandler senderConnection, String filename, byte[] hash, int filesize, User receiver) {
        this.sender = sender;
        this.senderFileTransferHandler = senderConnection;
        this.filename = filename;
        this.hash = hash;
        this.filesize = filesize;
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public FileTransferHandler getSenderFileTransferHandler() {
        return senderFileTransferHandler;
    }

    public FileTransferHandler getReceiverFileTransferHandler() {
        return receiverFileTransferHandler;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getHash() {
        return hash;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setReceiverFileTransferHandler(FileTransferHandler receiverFileTransferHandler) {
        this.receiverFileTransferHandler = receiverFileTransferHandler;
    }
}
