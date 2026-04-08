package ken.tar.sa_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import ken.tar.sa_backend.config.LoggerFactory;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.repository.ClientRepository;
import ken.tar.sa_backend.service.ClientService;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final Logger logger;
    private final ClientRepository theClientRepository;
    private final ObjectMapper objectMapper;

    public ClientServiceImpl(LoggerFactory loggerFactory, ClientRepository clientRepository, ObjectMapper objectMapper) {
        this.logger = loggerFactory.getLogger(ClientServiceImpl.class);
        theClientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Client client){
        logger.info("Checking client {} in data base.", client);
        Client clientDansLaBDD = theClientRepository.findByEmail(client.getEmail());
        if(clientDansLaBDD == null) {
            logger.info("Client {} not found in data base.", client);
            logger.info("Saving client {} ", client);
            theClientRepository.save(client);
        }
    }

    @Override
    public Client getClient(int id) {
        logger.info("Retrieving client with id {} from data base.", id);
        Optional<Client> optionalClient = theClientRepository.findById(id);
        return optionalClient.orElseThrow( () -> {
                logger.warn("Client with id {} not found in data base.", id);
                return  new EntityNotFoundException("Aucun client n'existe avec cet id");
            }
        );
    }

    @Override
    @Transactional
    public Client readOrCreate(Client client) {
        logger.info("Checking client with email {} in data base.", client.getEmail());
        Client existing = theClientRepository.findByEmail(client.getEmail());
        if (existing != null) {
            logger.info("Client with email {} already exists in data base.", client.getEmail());
            return existing;
        } else {
            logger.info("Client with email {} not found in data base. Creating new client.", client.getEmail());
            return theClientRepository.save(client);
        }
    }

    @Override
    public List<Client> getClients() {
        logger.info("Retrieving all clients");
        return theClientRepository.findAll();
    }

    @Override
    public Client update(int id, Client client) {
        logger.info("Checking client with id {} in data base.", id);
        Client savedClient = getClient(id);
        logger.info("Client with id {} found in data base.", id);

        if(savedClient.getId() == client.getId()) {
            logger.info("Updating client with id {} in data base.", id);
            savedClient.setEmail(client.getEmail());
            savedClient.setTelephone(client.getTelephone());
            theClientRepository.save(savedClient);
        }

        return  savedClient;
    }

    @Override
    public Client merge(Client client) {
       logger.info("Merging client {} in data base.", client);
       return theClientRepository.merge(client);
    }

    @Override
    public Client applyPatch(Map<String, Object> patchPayload, Client tempClient) {

        logger.info("Patching client with id {} using payload {}", tempClient.getId(), patchPayload);

        // Convert Client object to a JSON object node
        ObjectNode clientNode = objectMapper.convertValue(tempClient, ObjectNode.class);

        // Convert the patchPayload map to a JSON object node
        ObjectNode patchNode = objectMapper.convertValue(patchPayload, ObjectNode.class);

        // Merge the patch updates into the employee node
        clientNode.setAll(patchNode);

        return objectMapper.convertValue(clientNode, Client.class);
    }

}
