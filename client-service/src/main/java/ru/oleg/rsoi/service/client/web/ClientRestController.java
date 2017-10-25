package ru.oleg.rsoi.service.client.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    ClientService clientService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ClientResponse getClient(@PathVariable Integer id) {
        logger.debug("CLIENT: Getting client " + id);
        return clientService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<ClientResponse> getClients(Pageable page) {
        logger.debug("CLIENT: getting page " + page);
        Page<Client> clients = clientService.get(page);
        return clients.map(Client::toResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ClientResponse saveClient(@RequestBody ClientRequest clientRequest,
                                     HttpServletResponse response) {
        logger.debug("CLIENT: creating client " + clientRequest);
        Client client = clientService.save(clientRequest);
        response.addHeader(HttpHeaders.LOCATION, "/client/" + client.getId());
        return client.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteClient(@PathVariable Integer id) {
        logger.debug("CLIENT: deleting client " + id);
        clientService.deleteById(id);
    }

}
