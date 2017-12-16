package ru.oleg.rsoi.service.client.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.oleg.rsoi.dto.client.ClientRequest;
import ru.oleg.rsoi.service.client.domain.Client;
import ru.oleg.rsoi.service.client.repository.ClientRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClientServiceImplTest {

    @MockBean
    private ClientRepository repository;

    @Autowired
    private ClientServiceImpl clientService;
/*
    @Test
    public void get() {
        // Arrange
        PageRequest pq = new PageRequest(2, 3);

        List<Client> clientList = new ArrayList<>();
        clientList.add(new Client(1, "abc"));
        clientList.add(new Client(2, "qqq"));
        clientList.add(new Client(150, "test"));

        given(repository.findAll(pq)).willReturn(new PageImpl<Client>(clientList, pq, 100));

        // Act
        Page<Client> clients = clientService.get(pq);

        // Assert
        assertThat(clients).containsExactlyElementsOf(clientList);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getByIdThrowsOnNonExistingClientTest() {
        // Arrange
        given(repository.findOne(anyInt())).willReturn(null);

        // Act
        clientService.getById(2);
    }

    @Test
    public void getByIdReturnsClient() {
        // Arrange
        Client client = new Client(100, "tester");
        given(repository.findOne(100)).willReturn(client);

        // Act
        Client result = clientService.getById(100);

        // Assert
        assertThat(result).isEqualTo(client);
    }
*/
    @Test
    public void save() {
        //Arrange
        Client client = new Client().setUsername("tester");
        ClientRequest request = new ClientRequest("tester");

        given(repository.save(client)).willReturn(client);

        // Act
        Client result = clientService.save(request);

        // Assert
        assertThat(result.getUsername()).isEqualTo("tester");
        verify(repository, times(1)).save(client);
    }

    @Test
    public void deleteById() {
        // Act
        clientService.deleteById(404);

        // Assert
        verify(repository, times(1)).delete(404);
    }

}