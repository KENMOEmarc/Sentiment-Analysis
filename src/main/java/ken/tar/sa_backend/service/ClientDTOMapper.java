package ken.tar.sa_backend.service;

import ken.tar.sa_backend.dto.ClientDTO;
import ken.tar.sa_backend.entity.Client;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ClientDTOMapper implements Function<Client, ClientDTO> {
    @Override
    public ClientDTO apply(Client client) {
        return new ClientDTO(client.getId(), client.getEmail(), client.getTelephone());
    }
}
