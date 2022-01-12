package chatapp.server.clientthreads.filetransfer;

import chatapp.server.ServerGlobals;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class FileTransferHandler extends Thread {
    private static final Random random = new Random();

    private ServerGlobals globals;
    private Socket socket;
    private byte[] key = new byte[8];
    private FileTransferHandler target;

    public FileTransferHandler(Socket socket, ServerGlobals globals) {
        this.socket = socket;
        this.globals = globals;

        do {
            random.nextBytes(key);
        } while (globals.fileTransferHandlers.containsKey(key));
        globals.fileTransferHandlers.put(key, this);
    }

    @Override
    public void run() {
        try {
            socket.getOutputStream().write(key);
            while (!Thread.currentThread().isInterrupted()) {
                if (target != null) {
                    socket.getInputStream().transferTo(target.getSocket().getOutputStream());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public byte[] getKey() {
        return key;
    }

    public void setTarget(byte[] key) {
        target = globals.fileTransferHandlers.get(key);
    }
}
