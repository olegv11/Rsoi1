package ru.oleg.rsoi.remoteservice;

import lombok.Data;

@Data
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
