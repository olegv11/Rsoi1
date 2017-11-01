package ru.oleg.rsoi.remoteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.oleg.rsoi.dto.movie.MovieResponse;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.movie.RatingRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class RemoteMovieServiceImpl implements RemoteMovieService {

    private final RemoteRsoiServiceImpl<MovieRequest, MovieResponse> remoteService;
    private final RemoteRsoiServiceImpl<RatingRequest, Void> remoteRateService;

    @Autowired
    public RemoteMovieServiceImpl(@Value("${urls.services.movies}") String movieServiceUrl) {
        remoteService = new RemoteRsoiServiceImpl<>(movieServiceUrl, MovieResponse.class, MovieResponse[].class);
        remoteRateService = new RemoteRsoiServiceImpl<>(movieServiceUrl, Void.class, Void[].class);
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
    public Page<MovieResponse> movies(Pageable page) {
        Page<HashMap<String, Object>> response = remoteService.findAllPaged(page, "/movie");
        List<HashMap<String, Object>> responseMap = response.getContent();

        List<MovieResponse> movies = new ArrayList<>();
        for (HashMap<String, Object> map : response) {
            movies.add(new MovieResponse()
                    .setMovieId((Integer)map.get("movieId"))
                    .setDescription((String)map.get("description"))
                    .setName((String)map.get("name")));
        }

        //return remoteService.findAllPaged(page, "/movie");
        return new PageImpl<MovieResponse>(movies, page, response.getTotalElements());
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

    @Override
    public void rateMovie(int userId, int movieId, int score) {
        RatingRequest request = new RatingRequest(score, movieId, userId);
        remoteRateService.create(request, "/movie/rate");
    }
}
