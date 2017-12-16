package ru.oleg.rsoi.service.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.oleg.rsoi.dto.client.ClientRequest;
import ru.oleg.rsoi.dto.client.ClientResponse;
import ru.oleg.rsoi.service.client.domain.Client;
import ru.oleg.rsoi.service.client.service.ClientService;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ClientRestController.class)
public class ClientRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ClientService service;
/*
    @Test
    public void getExistingClientReturnsNormally() throws Exception {
        // Arrange
        Client client = new Client(100, "Smith");
        ClientResponse response = client.toResponse();
        given(service.getById(100)).willReturn(client);

        // Act/assert
        mvc.perform(get("/client/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(client.getId())))
                .andExpect(jsonPath("$.name", is(client.getName())));
    }

    @Test
    public void getNonexistentClientReturnsError() throws Exception {
        // Arrange
        given(service.getById(100)).willThrow(new EntityNotFoundException("Client not found"));

        // Act/assert
        mvc.perform(get("/client/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveClient() throws Exception {
        // Arrange
        Client client = new Client(506, "Bill");
        ClientRequest clientRequest = new ClientRequest("Bill");
        given(service.save(clientRequest)).willReturn(client);

        // Act/assert
        mvc.perform(post("/client/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(clientRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/client/506"));
    }

    @Test
    public void getNonEmptyPage() throws Exception {
        // Arrange
        List<Client> clients = new ArrayList<>();
        clients.add(new Client(10, "Jack"));
        clients.add(new Client(11, "David"));
        clients.add(new Client(12, "John"));

        PageRequest pr = new PageRequest(2, 3);
        PageImpl<Client> clientPage = new PageImpl<>(clients, pr, 100);

        given(service.get(pr)).willReturn(clientPage);

        // Act/assert
        mvc.perform(get("/client?page=2&size=3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(100)))
                .andExpect(jsonPath("$.size", is(3)))
                .andExpect(jsonPath("$.number", is(2)))
                .andExpect(jsonPath("$.content.size()", is(3)))
                .andExpect(jsonPath("$.content[0].id", is(10)))
                .andExpect(jsonPath("$.content[0].name", is("Jack")))
                .andExpect(jsonPath("$.content[1].id", is(11)))
                .andExpect(jsonPath("$.content[1].name", is("David")))
                .andExpect(jsonPath("$.content[2].id", is(12)))
                .andExpect(jsonPath("$.content[2].name", is("John")));
    }
*/
    @Test
    public void getEmptyPage() throws Exception {
        // Arrange
        List<Client> clients = new ArrayList<>();


        PageRequest pr = new PageRequest(0, 3);
        PageImpl<Client> clientPage = new PageImpl<>(clients, pr, 0);

        given(service.get(pr)).willReturn(clientPage);

        // Act/assert
        mvc.perform(get("/client?page=0&size=3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.size", is(3)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.content.size()", is(0)));
    }

    @Test
    public void deletePage() throws Exception {
        mvc.perform(delete("/client/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void generalError() throws Exception {
        // Arrange
        given(service.getById(anyInt())).willThrow(new RuntimeException("Test"));

        // Act/assert
        mvc.perform(get("/client/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}