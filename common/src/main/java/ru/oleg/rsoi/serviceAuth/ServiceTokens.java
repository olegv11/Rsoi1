package ru.oleg.rsoi.serviceAuth;

import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Component
public class ServiceTokens {
    String accessToken;
    String refreshToken;
}
