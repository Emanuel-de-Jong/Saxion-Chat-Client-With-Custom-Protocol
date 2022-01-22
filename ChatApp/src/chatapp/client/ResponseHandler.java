package chatapp.client;

import chatapp.client.interfaces.ServerConnectionListener;
import chatapp.shared.enums.ChatPackageType;
import chatapp.shared.models.chatpackages.ChatPackage;
import chatapp.shared.models.chatpackages.ErPackage;
import chatapp.shared.models.chatpackages.OkPackage;

import java.util.HashMap;
import java.util.function.Consumer;

public class ResponseHandler implements ServerConnectionListener {

    private static final long MILLIS_FOR_CANCEL = 3 * 1_000L;

    private final Runnable success;
    private final String message;
    private final ClientGlobals globals;
    private final HashMap<Integer, Consumer<String>> fails;

    public ResponseHandler(String message, Runnable success, ClientGlobals globals) {
        this(message, success, null, globals);
    }

    public ResponseHandler(String message, Runnable success, HashMap<Integer, Consumer<String>> fails, ClientGlobals globals) {
        this.success = success;
        this.fails = fails;
        this.message = message;
        this.globals = globals;

        //subscribe to serverConnection.
        this.globals.clientListeners.serverConnection.add(this);

        //unsubscribe after x time from serverConnection.
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
                success.run();
                globals.clientListeners.serverConnection.remove(this);
            }
        } else if (fails != null && chatPackage.getType() == ChatPackageType.ER) {
            ErPackage erPackage = (ErPackage) chatPackage;
            if (fails.containsKey(erPackage.getCode())) {
                Consumer<String> fail = fails.get(erPackage.getCode());
                if (fail != null) {
                    fail.accept(erPackage.getMessage());
                }
            } else if (fails.containsKey(-1)) {
                Consumer<String> fail = fails.get(-1);
                if (fail != null) {
                    fail.accept(erPackage.getMessage());
                }
            }
        }
    }
}
