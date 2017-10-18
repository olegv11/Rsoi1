package ru.oleg.rsoi.remoteservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class RemoteRsoiServiceImpl<Request, Response> implements RemoteRsoiService<Request, Response> {

    private final Class<Response> type;
    private final Class<Response[]> typeArray;
    private final String serviceUrl;

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
        ResponseEntity<Response> response =
            rt.postForEntity(getUrl(postfix), request, type);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RemoteServiceException("Request(" + request.toString() + ") was not created");
        }

        return response.getBody();
    }

    @Override
    public Response find(int id, String postfix) {
        ResponseEntity<Response> response =
                rt.getForEntity(getUrl(postfix), type, id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return null;
    }

    @Override
    public List<Response> findAll(int id, String postfix) {
        ResponseEntity<Response[]> response
                = rt.getForEntity(getUrl(postfix), typeArray, id);


        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(response.getBody());
        }

        return null;
    }

    @Override
    public void update(int id, Request request, String postfix) {
        rt.put(getUrl(postfix), request, id);
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
