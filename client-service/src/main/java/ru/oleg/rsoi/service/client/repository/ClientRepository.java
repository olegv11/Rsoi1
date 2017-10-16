package ru.oleg.rsoi.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.client.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
