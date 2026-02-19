package ken.tar.sa_backend.repository;

import jakarta.transaction.Transactional;
import ken.tar.sa_backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    Client findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Client c SET c.email = ?1, c.telephone = ?2 WHERE c.id = ?3")
    Client merge(Client client);
}
