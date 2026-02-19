package ken.tar.sa_backend.service;

import jakarta.persistence.EntityNotFoundException;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository theClientRepository;

    public ClientService( ClientRepository clientRepository) {
        theClientRepository = clientRepository;
    }

    public void save(Client client){
        Client clientDansLaBDD = theClientRepository.findByEmail(client.getEmail());
        if(clientDansLaBDD == null) {
            theClientRepository.save(client);
        }
    }

    public Client getClient(int id) {
        Optional<Client> optionalClient = theClientRepository.findById(id);
        return optionalClient.orElseThrow(
                () -> new EntityNotFoundException("Aucun client n'existe avec cet id")
        );
    }

    public Client readOrCreate(Client client){
        Client theClient = theClientRepository.findByEmail(client.getEmail());
        if(theClient == null) {
            theClient = theClientRepository.save(client);
        }
        return theClient;
    }

    public List<Client> getClients() {
        return theClientRepository.findAll();
    }

    public Client update(int id, Client client) {
        Client savedClient = getClient(id);

        if(savedClient.getId() == client.getId()) {
            savedClient.setEmail(client.getEmail());
            savedClient.setTelephone(client.getTelephone());
            theClientRepository.save(savedClient);
        }

        return  savedClient;
    }

    public Client merge(Client client) {
       return theClientRepository.merge(client);
    }

}
