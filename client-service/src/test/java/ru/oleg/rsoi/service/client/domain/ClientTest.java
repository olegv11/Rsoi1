package ru.oleg.rsoi.service.client.domain;

import org.junit.Test;
import ru.oleg.rsoi.dto.client.ClientResponse;

import static org.junit.Assert.*;

public class ClientTest {

    @Test
    public void toResponseTest()  {
        ClientResponse createdResponse = new Client()
                .setId(2)
                .setName("abc")
                .toResponse();
        ClientResponse trueResponse = new ClientResponse(2, "abc");

        assertEquals(createdResponse, trueResponse);
    }

}