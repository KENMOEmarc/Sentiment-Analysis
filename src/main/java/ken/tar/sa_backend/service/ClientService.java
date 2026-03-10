package ken.tar.sa_backend.service;

import ken.tar.sa_backend.entity.Client;
import java.util.List;
import java.util.Map;

public interface ClientService {
    void save(Client client);
    Client getClient(int id);
    Client readOrCreate(Client client);
    List<Client> getClients();
    Client update(int id, Client client);
    Client merge(Client client);

    Client applyPatch(Map<String, Object> patchPayload, Client tempClient);
}