package ru.oleg.rsoi.remoteservice;

import ru.oleg.rsoi.dto.MovieResponse;

public interface RemoteMovieService {
    MovieResponse createMovie(String name, String description) throws RemoteServiceException;
    MovieResponse findMovie(int id);
    void updateMovie(int id, String name, String description);
    boolean movieExists(int id);
    void deleteMovie(int id) throws RemoteServiceException;
}
