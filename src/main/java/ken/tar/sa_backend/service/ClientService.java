package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Client;
import java.util.List;

public interface ClientService {
    void save(Client client);
    Client getClient(int id);
    Client readOrCreate(Client client);
    List<Client> getClients();
    Client update(int id, Client client);
    Client merge(Client client);
}