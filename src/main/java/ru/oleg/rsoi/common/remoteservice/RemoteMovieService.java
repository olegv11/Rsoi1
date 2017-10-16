package ru.oleg.rsoi.common.domain.movies;

import ru.oleg.rsoi.common.domain.movies.movie.MovieResponse;

public interface RemoteMovieService {
    MovieResponse findMovie(int id);
    boolean movieExists(int id);
}
