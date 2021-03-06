package ru.oleg.rsoi.service.client.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oleg.rsoi.dto.client.ClientRequest;
import ru.oleg.rsoi.service.client.domain.Client;


public interface ClientService {
    Page<Client> get(Pageable page);
    Client getByUsername(String username);
    Client getById(Integer id);
    Client save(ClientRequest clientRequest);
    void deleteById(Integer id);
}
