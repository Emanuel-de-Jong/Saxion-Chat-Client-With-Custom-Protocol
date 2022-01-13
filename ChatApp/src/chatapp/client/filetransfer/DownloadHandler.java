package chatapp.client.filetransfer;

import chatapp.client.ClientGlobals;
import chatapp.shared.Globals;
import chatapp.shared.models.Message;
import chatapp.shared.models.User;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.Socket;

public class DownloadHandler extends Thread { //todo: decide if this should be a thread
    private ClientGlobals globals;
    private User sender;
    private String fileName;
    private int fileSize;
    private byte[] hash;
    private byte[] connection;
    private File file;


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

    }


    public void run() {
        sender.setChatAdded(true);
        sender.addPrivateMessage(new Message(sender + " requests to send file: " + fileName + " (" + fileSize + " bytes).", null));
        if (JOptionPane.showConfirmDialog(null, sender + " requests to send file: " + fileName + " (" + fileSize + " bytes).", "Filetransfer", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JFileChooser fc = new JFileChooser();
            String fileNameExtension = getFileExtension(fileName);
            fc.setFileFilter(new FileNameExtensionFilter(fileNameExtension,fileNameExtension));
            fc.setSelectedFile(new File(fileName));
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();

                if (fileNameExtension != null && !fileNameExtension.equals(getFileExtension(file.getName()))) {
                    globals.systemHelper.log("OLD" + file.getAbsolutePath());
                    file.renameTo(new File(file.getAbsolutePath() + '.' + getFileExtension(fileName)));
                }

                globals.systemHelper.log("NEW"+ file.getAbsolutePath());

                try {
                    startFileTransfer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else rejectDownload();
        } else rejectDownload();
    }

    private void startFileTransfer() throws IOException {
        socket = new Socket(Globals.IP, Globals.PORT + 1);
        in = new BufferedInputStream(socket.getInputStream());
        out = new BufferedOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                connection = in.readNBytes(8);
                acceptDownload();

                fileOutStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                fileOutStream.write(in.readNBytes(fileSize));
                fileOutStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutStream != null) fileOutStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void acceptDownload() {
        if (connection == null) throw new IllegalStateException("Connection is not set yet.");
        sender.addPrivateMessage(new Message(globals.currentUser + " accepted the File transfer of file: " + fileName, null));
        globals.clientListeners.downloads.forEach(downloadListener -> downloadListener.acceptDownload(sender, hash, connection));
    }

    private void rejectDownload() {
        sender.addPrivateMessage(new Message(globals.currentUser + " rejected the File transfer of file: " + fileName, null));
    }

    private String getFileExtension(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length == 0) return null;
        return parts[parts.length - 1];
    }
}
