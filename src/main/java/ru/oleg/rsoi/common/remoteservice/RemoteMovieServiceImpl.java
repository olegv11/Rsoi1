package ru.oleg.rsoi.common.domain.movies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.common.domain.movies.movie.MovieResponse;

@Component
public class RemoteMovieServiceImpl implements RemoteMovieService {

    @Value("${urls.services.movies}")
    String movieServiceUrl;

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
    public boolean movieExists(int id) {
        return findMovie(id) != null;
    }
}
