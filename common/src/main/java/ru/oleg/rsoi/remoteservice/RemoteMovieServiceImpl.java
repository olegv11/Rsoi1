package ru.oleg.rsoi.remoteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    private final RemoteRsoiServiceImpl<MovieRequest, MovieResponse> remoteService;


    @Autowired
    public RemoteMovieServiceImpl(@Value("${urls.services.movies}") String movieServiceUrl) {
        remoteService = new RemoteRsoiServiceImpl<>(movieServiceUrl, MovieResponse.class, MovieResponse[].class);
    }

    @Override
    public MovieResponse createMovie(String name, String description) throws RemoteServiceException {
        MovieRequest mr = new MovieRequest(name, description);
        return remoteService.create(mr, "/movie");
    }

    @Override
    public MovieResponse findMovie(int id) {
        return remoteService.find(id, "/movie/{id}");
    }

    @Override
    public void updateMovie(int id, String name, String description) {
        MovieRequest mr = new MovieRequest(name, description);
        remoteService.update(id, mr, "/movie/{id}");
    }

    @Override
    public boolean movieExists(int id) {
        return remoteService.exists(id, "/movie/{id}");
    }

    @Override
    public void deleteMovie(int id) throws RemoteServiceException {
        remoteService.delete(id, "/movie/{id}");
    }
}
