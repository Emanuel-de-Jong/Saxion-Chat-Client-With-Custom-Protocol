package chatapp.server.Data;

import chatapp.server.models.Client;

import java.util.ArrayList;

public class Clients extends ArrayList<Client> {

    public Client getByName(String name) {
        for (Client client : this) {
            if (client.getName().equals(name)) {
                return client;
            }
        }
        return null;
    }

}
