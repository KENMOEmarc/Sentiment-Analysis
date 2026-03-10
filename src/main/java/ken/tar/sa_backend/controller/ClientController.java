package ken.tar.sa_backend.controller;

import jakarta.persistence.EntityNotFoundException;
import ken.tar.sa_backend.entity.Client;
import ken.tar.sa_backend.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public void createClient(@RequestBody Client client){
        this.clientService.save(client);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping
    public List<Client> getAllClients() {
        return this.clientService.getClients();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path="/{id}")
    public Client getClient(@PathVariable int id) {
        return this.clientService.getClient(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping(path = "{id}")
    public void modifier(@PathVariable int id, @RequestBody Client client) {
        this.clientService.update(id, client);
    }

    @PatchMapping("/{id}")
    public Client patchClient(@PathVariable int id, @RequestBody Map<String, Object> patchPayload) {
        Client tempClient = clientService.getClient(id);

        // throw exception if null
        if (tempClient == null) {
            throw new EntityNotFoundException("Client id not found - " + id);
        }

        // throw exception if request body contains "id" key
        if (patchPayload.containsKey("id")) {
            throw new RuntimeException("Client id not allowed in request body - " + id);
        }

        Client patchedClient = clientService.applyPatch(patchPayload, tempClient);

        Client dbClient = clientService.update(patchedClient.getId(), patchedClient);

        return dbClient;
    }

}
