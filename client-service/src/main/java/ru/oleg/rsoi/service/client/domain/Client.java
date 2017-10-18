package ru.oleg.rsoi.service.client.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.ClientResponse;

import javax.persistence.*;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    public ClientResponse toResponse() {
        return new ClientResponse(id, name);
    }
}
