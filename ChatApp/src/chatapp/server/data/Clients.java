package chatapp.server.data;

import chatapp.server.models.Client;

import java.util.ArrayList;

public class Clients extends ArrayList<Client> {

    public Client getByName(String name) {
        for (Client client : this) {
            if (name.equals(client.getName())) {
                return client;
            }
        }
        return null;
    }

}
