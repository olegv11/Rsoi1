package ru.oleg.rsoi.service.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.oleg.rsoi.dto.ClientResponse;
import ru.oleg.rsoi.service.client.service.ClientService;

@RestController
@RequestMapping("/client")
public class ClientRestController {
    @Autowired
    ClientService clientService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ClientResponse getClient(@PathVariable Integer id) {
        return clientService.getById(id).toResponse();
    }

}
