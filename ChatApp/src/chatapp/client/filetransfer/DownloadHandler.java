package chatapp.client.filetransfer;

import chatapp.client.ClientGlobals;
import chatapp.shared.Globals;
import chatapp.shared.models.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class DownloadHandler extends Thread{
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

    public DownloadHandler(User sender, String fileName, int fileSize, byte[] hash,ClientGlobals globals) {
        this.globals = globals;
        this.sender = sender;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.hash = hash;

    }


    public void run() {
        if (JOptionPane.showConfirmDialog(null,"", "Filetransfer",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(fileName));
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                try {
                    startFileTransfer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
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

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOutStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void acceptDownload() {
        if (connection != null) return;
        globals.clientListeners.downloads.forEach(downloadListener -> downloadListener.acceptDownload(sender,hash,connection));
    }
}
