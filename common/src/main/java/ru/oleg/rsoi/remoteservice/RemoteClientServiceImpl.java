package ru.oleg.rsoi.remoteservice;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

@Component
public class RemoteClientServiceImpl implements RemoteClientService {
    @Override
    public boolean isAuthenticated(String token) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic Z2F0ZXdheUNsaWVudDpnYXRld2F5U2VjcmV0Cg");

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8093/oauth/check_token")
                .queryParam("token", token);
        try {
            ResponseEntity<String> response =
                    rt.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        }
        catch (HttpClientErrorException e) {
            return false;
        }
    }

    @Override
    public TokenPair refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic Z2F0ZXdheUNsaWVudDpnYXRld2F5U2VjcmV0Cg");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);

        ResponseEntity<HashMap> response = null;
        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            RestTemplate rt = new RestTemplate();
            response = rt.postForEntity("http://localhost:8093/oauth/token",
                    request, HashMap.class);
        } catch (RestClientException e) {
            return null;
        }

        TokenPair result = new TokenPair();
        result.setAccessToken((String)response.getBody().get("access_token"));
        result.setRefreshToken((String)response.getBody().get("refresh_token"));
        return result;
    }

    @Override
    public boolean isAdmin(String token) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8093/client/isAdmin")
                .queryParam("token", token);

        ResponseEntity<Boolean> response;
        try {
            RestTemplate rt = new RestTemplate();
            response = rt.getForEntity(builder.build().encode().toUri(), Boolean.class);
        } catch (RestClientException e) {
            return false;
        }

        return response.getBody();
    }

    @Override
    public void logout(String token) {

        ResponseEntity<Void> response;
        try {
            RestTemplate rt = new RestTemplate();
            response = rt.postForEntity("http://localhost:8093/client/logout", token, Void.class);
        } catch (RestClientException e) {
        }
    }
}
