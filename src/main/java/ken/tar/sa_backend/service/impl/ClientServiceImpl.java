package ken.tar.sa_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityNotFoundException;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.repository.ClientRepository;
import ken.tar.sa_backend.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository theClientRepository;
    private final ObjectMapper objectMapper;

    public ClientServiceImpl(ClientRepository clientRepository, ObjectMapper objectMapper) {
        theClientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Client client){
        Client clientDansLaBDD = theClientRepository.findByEmail(client.getEmail());
        if(clientDansLaBDD == null) {
            theClientRepository.save(client);
        }
    }

    @Override
    public Client getClient(int id) {
        Optional<Client> optionalClient = theClientRepository.findById(id);
        return optionalClient.orElseThrow(
                () -> new EntityNotFoundException("Aucun client n'existe avec cet id")
        );
    }

    @Override
    public Client readOrCreate(Client client){
        Client theClient = theClientRepository.findByEmail(client.getEmail());
        if(theClient == null) {
            theClient = theClientRepository.save(client);
        }
        return theClient;
    }

    @Override
    public List<Client> getClients() {
        return theClientRepository.findAll();
    }

    @Override
    public Client update(int id, Client client) {
        Client savedClient = getClient(id);

        if(savedClient.getId() == client.getId()) {
            savedClient.setEmail(client.getEmail());
            savedClient.setTelephone(client.getTelephone());
            theClientRepository.save(savedClient);
        }

        return  savedClient;
    }

    @Override
    public Client merge(Client client) {
       return theClientRepository.merge(client);
    }

    @Override
    public Client applyPatch(Map<String, Object> patchPayload, Client tempClient) {

        // Convert Client object to a JSON object node
        ObjectNode clientNode = objectMapper.convertValue(tempClient, ObjectNode.class);

        // Convert the patchPayload map to a JSON object node
        ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);

        // Merge the patch updates into the employee node
        clientNode.setAll(patchNode);

        return objectMapper.convertValue(clientNode, Client.class);
    }

}
