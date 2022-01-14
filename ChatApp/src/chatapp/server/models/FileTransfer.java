package chatapp.server.models;

import chatapp.server.clientthreads.filetransfer.FileTransferHandler;
import chatapp.shared.models.User;

public class FileTransfer {
    private User sender;
    private User receiver;

    private FileTransferHandler senderFileTransferHandler;
    private FileTransferHandler receiverFileTransferHandler;

    private String filename;
    private byte[] hash;
    private int filesize;

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
