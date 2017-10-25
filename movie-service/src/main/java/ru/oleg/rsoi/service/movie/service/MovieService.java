package ru.oleg.rsoi.service.movie.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.movie.RatingRequest;
import ru.oleg.rsoi.service.movie.domain.Movie;

import java.util.List;

public interface MovieService {
    Movie getById(Integer id);
    Movie updateById(Integer id, MovieRequest movieRequest);
    List<Movie> getByName(String name);
    Page<Movie> get(Pageable page);
    Movie save(MovieRequest movieRequest);
    void deleteById(Integer id);

    void rateMovie(RatingRequest ratingRequest);

}
