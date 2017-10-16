package ru.oleg.rsoi.remoteservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.dto.MovieResponse;
import ru.oleg.rsoi.dto.MovieRequest;

import java.rmi.RemoteException;

@Component
public class RemoteMovieServiceImpl implements RemoteMovieService {

    @Value("${urls.services.movies}")
    String movieServiceUrl;

    @Override
    public MovieResponse createMovie(String name, String description) throws RemoteServiceException {
        MovieRequest mr = new MovieRequest(name, description);
        RestTemplate rt = new RestTemplate();

        ResponseEntity<MovieResponse> re = rt.postForEntity(movieServiceUrl + "/movie/", mr,
                MovieResponse.class);

        if (re.getStatusCode() != HttpStatus.CREATED) {
            throw new RemoteServiceException("Movie was not created");
        }

        return re.getBody();
    }

    @Override
    public MovieResponse findMovie(int id) {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<MovieResponse> response = rt.getForEntity(movieServiceUrl + "/movie/{id}",
                MovieResponse.class, id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return null;

    }

    @Override
    public void updateMovie(int id, String name, String description) {
        MovieRequest mr = new MovieRequest(name, description);
        RestTemplate rt = new RestTemplate();
        rt.put(movieServiceUrl + "/movie/{id}", mr, id);
    }

    @Override
    public boolean movieExists(int id) {
        return findMovie(id) != null;
    }

    @Override
    public void deleteMovie(int id) throws RemoteServiceException {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<Void> re = rt.exchange(movieServiceUrl + "/movie/{id}",
                HttpMethod.DELETE, null, Void.class, id);

        if (re.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RemoteServiceException("Movie was not deleted");
        }
    }
}
