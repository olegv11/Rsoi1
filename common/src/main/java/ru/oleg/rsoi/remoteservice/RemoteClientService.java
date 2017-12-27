package ru.oleg.rsoi.remoteservice;

import java.util.HashMap;

public interface RemoteClientService {
    boolean isAuthenticated(String token);
    TokenPair refreshToken(String refreshToken);
    boolean isAdmin(String token);
    void logout(String token);
}
