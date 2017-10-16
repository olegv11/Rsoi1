package ru.oleg.rsoi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oleg.rsoi.domain.movies.Movie;
import ru.oleg.rsoi.repository.movies.MovieRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    private Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public Movie getById(Integer id) {
        Movie movie = movieRepository.findOne(id);
        if (movie == null) {
            throw new EntityNotFoundException("Movie(" + id + ") not found");
        }
        return movie;
    }

    @Override
    public List<Movie> getByName(String name) {
        return movieRepository.findByName(name);
    }

    @Override
    public Page<Movie> get(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void deleteById(Integer id) {
        movieRepository.delete(id);
    }
}
