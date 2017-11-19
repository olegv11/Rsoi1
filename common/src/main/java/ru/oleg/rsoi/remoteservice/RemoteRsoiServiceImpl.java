package ru.oleg.rsoi.remoteservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.dto.ErrorResponse;
import ru.oleg.rsoi.errors.ApiErrorView;
import ru.oleg.rsoi.errors.ApiErrorViewException;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

public class RemoteRsoiServiceImpl<Request, Response> implements RemoteRsoiService<Request, Response> {

    private final Class<Response> type;
    private final Class<Response[]> typeArray;
    private final String serviceUrl;

    private ObjectMapper mapper = new ObjectMapper();

    public RemoteRsoiServiceImpl(String serviceUrl, Class<Response> responseClass, Class<Response[]> responseArrayClass) {
        this.serviceUrl = serviceUrl;
        this.type = responseClass;
        this.typeArray = responseArrayClass;
    }

    private final RestTemplate rt = new RestTemplate();

    @Override
    public String getUrl(String postfix) {
        return serviceUrl + postfix;
    }

    @Override
    public Response create(Request request, String postfix) {
        ResponseEntity<Response> response;

        try {
            response = rt.postForEntity(getUrl(postfix), request, type);
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw TryGetErrorView(e);
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

        try {
            response = rt.getForEntity(getUrl(postfix), type, id);
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw TryGetErrorView(e);
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
        try {
            response = rt.getForEntity(getUrl(postfix), typeArray, id);
        }
        catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
            {
                throw TryGetErrorView(e);
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
        ResponseEntity<RestResponsePage<HashMap<String, Object>>> response
                = rt.exchange(getUrl(postfix)+"?page="+page.getPageNumber()+"&size="+page.getPageSize(), HttpMethod.GET, null,
                new ParameterizedTypeReference<RestResponsePage<HashMap<String, Object>>>() {});

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().pageImpl();
        }

        return null;
    }

    @Override
    public void update(int id, Request request, String postfix) {
        rt.exchange(getUrl(postfix), HttpMethod.PUT, new HttpEntity<Request>(request), Void.class, id);
    }

    @Override
    public boolean exists(int id, String postfix) {
        return find(id, postfix) == null;
    }

    @Override
    public void delete(int id, String postfix) {
        rt.delete(getUrl(postfix), id);
    }
}

