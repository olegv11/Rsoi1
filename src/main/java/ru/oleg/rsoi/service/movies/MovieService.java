package ru.oleg.rsoi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oleg.rsoi.domain.movies.Movie;

import java.util.List;

public interface MovieService {
    Movie getById(Integer id);
    List<Movie>  getByName(String name);
    Page<Movie> get(Pageable page);
    Movie save(Movie movie);
    void deleteById(Integer id);

}
