package chatapp.client;

import chatapp.client.data.Groups;
import chatapp.client.data.Users;
import chatapp.shared.Globals;
import chatapp.shared.models.User;

public class ClientGlobals extends Globals {

    public User currentUser;
    public Groups groups;
    public Users users;

    public ClientListeners clientListeners = new ClientListeners();
    public AsymmetricEncryptionHelper asymmetricEncryptionHelper = new AsymmetricEncryptionHelper();
    public static boolean security = true;
}
