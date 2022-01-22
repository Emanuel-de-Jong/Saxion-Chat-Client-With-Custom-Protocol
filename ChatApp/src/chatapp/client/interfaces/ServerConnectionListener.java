package chatapp.client.interfaces;

import chatapp.shared.interfaces.Listener;
import chatapp.shared.models.chatpackages.ChatPackage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface ServerConnectionListener extends Listener {

    void chatPackageReceived(ChatPackage chatPackage) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException;

}
