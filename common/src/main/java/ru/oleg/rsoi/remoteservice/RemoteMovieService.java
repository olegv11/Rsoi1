package ru.oleg.rsoi.remoteservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oleg.rsoi.dto.movie.MovieResponse;

public interface RemoteMovieService {
    MovieResponse createMovie(String name, String description) throws RemoteServiceException;
    MovieResponse findMovie(int id);
    Page<MovieResponse> movies(Pageable movies);
    void updateMovie(int id, String name, String description);
    boolean movieExists(int id);
    void deleteMovie(int id) throws RemoteServiceException;
    void rateMovie(int userId, int movieId, int score);
}
