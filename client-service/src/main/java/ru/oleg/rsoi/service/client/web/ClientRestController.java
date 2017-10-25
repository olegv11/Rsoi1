package ru.oleg.rsoi.service.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.client.ClientRequest;
import ru.oleg.rsoi.dto.client.ClientResponse;
import ru.oleg.rsoi.service.client.domain.Client;
import ru.oleg.rsoi.service.client.service.ClientService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/client")
public class ClientRestController {
    @Autowired
    ClientService clientService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ClientResponse getClient(@PathVariable Integer id) {
        return clientService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<ClientResponse> getClients(Pageable page) {
        Page<Client> clients = clientService.get(page);
        return clients.map(Client::toResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ClientResponse saveClient(@RequestBody ClientRequest clientRequest,
                                     HttpServletResponse response) {
        Client client = clientService.save(clientRequest);
        response.addHeader(HttpHeaders.LOCATION, "/client/" + client.getId());
        return client.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteClient(@PathVariable Integer id) {
        clientService.deleteById(id);
    }

}
