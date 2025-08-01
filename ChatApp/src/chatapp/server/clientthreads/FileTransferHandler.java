package chatapp.server.clientthreads;

import chatapp.server.ServerGlobals;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class FileTransferHandler extends Thread {
    private static final Random random = new Random();

    private final ServerGlobals globals;
    private final Socket socket;
    private final byte[] key = new byte[8];
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
        //time out thread
        new Thread(() -> {
            try {
                Thread.sleep(5 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.interrupt();
        }).start();

        try {
            socket.getOutputStream().write(key);
            //until this program ends keep writing everything to the target as long as the target is not null
            while (!Thread.currentThread().isInterrupted()) {
                if (target != null) {
                    socket.getInputStream().transferTo(target.getSocket().getOutputStream());
//                    var enc = Base64.getEncoder(); // this piece of gets called way 2 many times so laggs out intelij if enabled!!!
//                    globals.systemHelper.log("Transfering: " + enc.encodeToString(key) + " to " + enc.encodeToString(target.key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public byte[] getKey() {
        return key;
    }

    public void setTarget(FileTransferHandler fileTransferHandler) {
        target = fileTransferHandler;
    }
}
