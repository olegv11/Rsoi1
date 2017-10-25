package ru.oleg.rsoi.service.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.oleg.rsoi.dto.client.ClientRequest;
import ru.oleg.rsoi.service.client.domain.Client;
import ru.oleg.rsoi.service.client.repository.ClientRepository;

import javax.persistence.EntityNotFoundException;


@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public Page<Client> get(Pageable page) {
        return clientRepository.findAll(page);
    }

    @Override
    public Client getById(Integer id) {
        Client client = clientRepository.findOne(id);
        if (client == null) {
            throw new EntityNotFoundException("Client("+id+") not found");
        }
        return client;
    }

    @Override
    public Client save(ClientRequest clientRequest) {
        Client client = new Client()
                .setName(clientRequest.getName());
        return clientRepository.save(client);
    }

    @Override
    public void deleteById(Integer id) {
        clientRepository.delete(id);
    }
}
