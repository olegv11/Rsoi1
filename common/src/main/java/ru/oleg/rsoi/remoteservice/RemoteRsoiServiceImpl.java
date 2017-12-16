package ru.oleg.rsoi.remoteservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.dto.ErrorResponse;
import ru.oleg.rsoi.errors.ApiErrorView;
import ru.oleg.rsoi.errors.ApiErrorViewException;
import ru.oleg.rsoi.serviceAuth.ServiceCredentials;
import ru.oleg.rsoi.serviceAuth.ServiceTokens;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

public class RemoteRsoiServiceImpl<Request, Response> implements RemoteRsoiService<Request, Response> {

    private final Class<Response> type;
    private final Class<Response[]> typeArray;
    private final String serviceUrl;
    private final ServiceCredentials myCredentials;
    private final ServiceTokens tokens;

    private ObjectMapper mapper = new ObjectMapper();

    public RemoteRsoiServiceImpl(String serviceUrl, ServiceCredentials myCredentials, ServiceTokens tokens, Class<Response> responseClass, Class<Response[]> responseArrayClass) {
        this.serviceUrl = serviceUrl;
        this.type = responseClass;
        this.typeArray = responseArrayClass;

        this.myCredentials = myCredentials;
        this.tokens = tokens;
    }


    public HttpEntity<Request> createEntity(Request request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (tokens != null && tokens.getAccessToken() != null && !tokens.getAccessToken().isEmpty()) {
            headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        }
        return new HttpEntity<>(request, headers);
    }

    public HttpEntity<Void> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (tokens != null && tokens.getAccessToken() != null && !tokens.getAccessToken().isEmpty()) {
            headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        }
        return new HttpEntity<>(headers);
    }

    private boolean refreshTokens() {
        if (tokens.getAccessToken() == null) {
            return receiveTokens();
        }

        try {
            ServiceTokens refreshed = rt.postForObject(getUrl("/auth/token"),
                    tokens.getRefreshToken(), ServiceTokens.class);
            tokens.setAccessToken(refreshed.getAccessToken());
            tokens.setRefreshToken(refreshed.getRefreshToken());
            return true;
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return receiveTokens();
            }
            return false;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private boolean receiveTokens() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + myCredentials.toString());

        try {
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ServiceTokens newTokens = rt.exchange(getUrl("/auth/token"), HttpMethod.GET, entity, ServiceTokens.class).getBody();
            tokens.setAccessToken(newTokens.getAccessToken());
            tokens.setRefreshToken(newTokens.getRefreshToken());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private final RestTemplate rt = new RestTemplate();

    @Override
    public String getUrl(String postfix) {
        return serviceUrl + postfix;
    }

    @Override
    public Response create(Request request, String postfix) {
        ResponseEntity<Response> response;

        HttpEntity<Request> entity = createEntity(request);

        try {
            response = rt.postForEntity(getUrl(postfix), entity, type);
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw TryGetErrorView(e);
            }

            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw TryGetError(e);
                return create(request, postfix);
            }
            throw TryGetError(e);
        }
        catch (ResourceAccessException access) {
            throw new RemoteServiceAccessException("Не удалось связаться с удалённым сервером", getUrl(postfix));
        }

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RemoteServiceException("Request(" + request.toString() + ") was not created");
        }

        return response.getBody();
    }


    @Override
    public Response find(int id, String postfix) {
        ResponseEntity<Response> response;

        HttpEntity<Void> entity = createEntity();

        try {
            response = rt.exchange(getUrl(postfix), HttpMethod.GET, entity, type, id);
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw TryGetErrorView(e);
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw TryGetError(e);
                return find(id, postfix);
            }
            throw TryGetError(e);
        }
        catch (ResourceAccessException access) {
            throw new RemoteServiceAccessException("Не удалось связаться с удалённым сервером", getUrl(postfix));
        }

        return response.getBody();
    }

    @Override
    public List<Response> findAll(int id, String postfix) {
        String url = getUrl(postfix);
        ResponseEntity<Response[]> response;

        HttpEntity<Void> entity = createEntity();

        try {
            response = rt.exchange(getUrl(postfix), HttpMethod.GET, entity, typeArray, id);
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw TryGetErrorView(e);
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw TryGetError(e);
                return findAll(id, postfix);
            }
            throw TryGetError(e);
        }
        catch (ResourceAccessException access) {
            throw new RemoteServiceAccessException("Не удалось связаться с удалённым сервером", getUrl(postfix));
        }


        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(response.getBody());
        }

        return null;
    }

    private RemoteServiceException TryGetError(HttpStatusCodeException e) throws RemoteServiceException {
        try
        {
            ErrorResponse errorResponse = mapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            return new RemoteServiceException(errorResponse.getMessage(), e.getStatusCode());
        }
        catch (IOException io) {
            return new RemoteServiceException("General error");
        }
    }

    private ApiErrorViewException TryGetErrorView(HttpStatusCodeException e) throws RemoteServiceException {
        try
        {
            return new ApiErrorViewException(mapper.readValue(e.getResponseBodyAsString(), ApiErrorView.class));
        }
        catch (IOException io) {
            return new ApiErrorViewException(new ApiErrorView(null, null));
        }
    }

    @Override
    public Page<HashMap<String, Object>> findAllPaged(Pageable page, String postfix) {

        ResponseEntity<RestResponsePage<HashMap<String, Object>>> response;
        HttpEntity<Void> entity = createEntity();

        try {
            response = rt.exchange(getUrl(postfix)+"?page="+page.getPageNumber()+"&size="+page.getPageSize(), HttpMethod.GET, entity,
            new ParameterizedTypeReference<RestResponsePage<HashMap<String, Object>>>() {});
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw TryGetErrorView(e);
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                if (!refreshTokens()) throw TryGetError(e);
                return findAllPaged(page, postfix);
            }
            throw TryGetError(e);
        }
        catch (ResourceAccessException access) {
            throw new RemoteServiceAccessException("Не удалось связаться с удалённым сервером", getUrl(postfix));
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().pageImpl();
        }

        return null;
    }

    @Override
    public void update(int id, Request request, String postfix) {
        HttpEntity<Request> entity = createEntity(request);

        try {
            rt.exchange(getUrl(postfix), HttpMethod.PUT, entity, Void.class, id);
        }
        catch (HttpStatusCodeException e) {
           if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
               if (!refreshTokens()) throw TryGetError(e);
               update(id, request, postfix);
           }
        }
    }

    @Override
    public boolean exists(int id, String postfix) {
        return find(id, postfix) == null;
    }

    @Override
    public void delete(int id, String postfix) {
        HttpEntity<Void> entity = createEntity();


        try {
            rt.exchange(getUrl(postfix), HttpMethod.DELETE, entity, Void.class, id);
        } catch (RestClientException e) {
            if (!refreshTokens()) throw e;
            delete(id, postfix);
        }
    }
}

