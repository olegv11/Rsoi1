package ru.oleg.rsoi.service.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.client.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
