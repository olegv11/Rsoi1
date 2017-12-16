package ru.oleg.rsoi.service.client.domain;

import com.google.common.hash.Hashing;
import lombok.*;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.client.ClientResponse;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String username;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static String passwordToHash(String password) {
        return Hashing.sha256().hashString(password + "myAwesomeSalt", StandardCharsets.UTF_8).toString();
    }


    public ClientResponse toResponse() {
        return new ClientResponse(id, username);
    }
}
