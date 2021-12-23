package chatapp.client;

import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.OkPackage;

import java.util.function.Consumer;

public class ResponseHandler implements ServerConnectionListener {

    private static final long MILLIS_FOR_CANCEL = 3 * 1_000L;

    private Runnable runnable;
    private String message;
    private ClientGlobals globals;

    public ResponseHandler(Runnable consumer, String message, ClientGlobals globals) {
        this.runnable = consumer;
        this.message = message;
        this.globals = globals;
        this.globals.clientListeners.serverConnection.add(this);
        new Thread(() -> {
            try {
                Thread.sleep(MILLIS_FOR_CANCEL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.globals.clientListeners.serverConnection.remove(this);
        }).start();
    }

    @Override
    public void chatPackageReceived(ChatPackage chatPackage) {
        if (chatPackage.getType() == ChatPackageType.OK) {
            OkPackage okPackage = (OkPackage) chatPackage;
            if (message == null || okPackage.getMessage().equals(message)) {
                runnable.run();
                globals.clientListeners.serverConnection.remove(this);
            }
        }
    }
}
