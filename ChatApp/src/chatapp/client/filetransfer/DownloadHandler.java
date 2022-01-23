package chatapp.client.filetransfer;

import chatapp.client.ClientGlobals;
import chatapp.shared.Globals;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class DownloadHandler extends Thread {
    private final ClientGlobals globals;
    private final User sender;
    private final String fileName;
    private final int fileSize;
    private final byte[] hash;
    private byte[] connection;
    private File file;
    private String outputFileName;


    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private DataOutputStream fileOutStream;

    public DownloadHandler(User sender, String fileName, int fileSize, byte[] hash, ClientGlobals globals) {
        this.globals = globals;
        this.sender = sender;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.hash = hash;

        closeAfterTimeout(5 * 60 * 1000);
    }


    public void run() {
        sender.setChatAdded(true);
        sender.addPrivateMessage(new Message(sender + " requests to send file: " + fileName + " (" + fileSize + " bytes).", null));
        //asks if the user wants to download this file
        if (JOptionPane.showConfirmDialog(null, sender + " requests to send file: " + fileName + " (" + fileSize + " bytes).", "Filetransfer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JFileChooser fc = new JFileChooser();
            String fileNameExtension = getFileExtension(fileName);
            fc.setFileFilter(new FileNameExtensionFilter(fileNameExtension, fileNameExtension));
            fc.setSelectedFile(new File(fileName));
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) { //ask where to save the file to
                file = fc.getSelectedFile();

                outputFileName = file.getAbsolutePath();
                //if filename extension is different add the original one to the end.
                if (fileNameExtension != null && !fileNameExtension.equals(getFileExtension(file.getName()))) {
                    outputFileName = file.getAbsolutePath() + "." + fileNameExtension;
                }

                try {
                    startFileTransfer();
                } catch (IOException e) {
                    close();
                    e.printStackTrace();
                }
            } else rejectDownload();
        } else rejectDownload();
    }

    /**
     * start the file transfer
     * @throws IOException
     */
    private void startFileTransfer() throws IOException {
        socket = new Socket(Globals.IP, Globals.PORT + 1);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());

        connection = in.readNBytes(8);
        acceptDownload();
        byte[] fileBytes = in.readNBytes(fileSize);
        if (!hashesMatch(fileBytes, hash)) {
            sender.addPrivateMessage(new Message(globals.currentUser + ": download failed (hash doesn't match) " + fileName + ".", null));
            return;
        }
        fileOutStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        fileOutStream.write(fileBytes);
        fileOutStream.close();
        file.renameTo(new File(outputFileName));
        socket.close();
        sender.addPrivateMessage(new Message(globals.currentUser + ": download completed " + fileName + ".", null));

    }

    /**
     * md5 hash check
     */
    private boolean hashesMatch(byte[] bytes, byte[] hash) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return Arrays.equals(md.digest(bytes), hash);
    }

    /**
     * send that you want to accept the download
     */
    private void acceptDownload() {
        if (connection == null) throw new IllegalStateException("Connection is not set yet.");
        sender.addPrivateMessage(new Message(globals.currentUser + " accepted the File transfer of file: " + fileName, null));
        globals.clientListeners.downloads.forEach(downloadListener -> downloadListener.acceptDownload(sender, hash, connection));
    }

    /**
     * send that you don't want to accept the download
     */
    private void rejectDownload() {
        sender.addPrivateMessage(new Message(globals.currentUser + " rejected the File transfer of file: " + fileName, null));
    }

    /**
     * get the last part after the dot of a file
     * @param fileName
     * @return
     */
    private String getFileExtension(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length == 0) return null;
        return parts[parts.length - 1];
    }

    /**
     * close after set timeout
     * @param timeout
     */
    private void closeAfterTimeout(int timeout) {
        new Thread(() -> {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            close();
        }).start();
    }

    /**
     * close the upload handler (if you don't it will never be removed by garbage collection)
     */
    private void close() {
        try {
            sender.addPrivateMessage(new Message(globals.currentUser + " failed to send file: " + fileName, null));
            socket.close();
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
