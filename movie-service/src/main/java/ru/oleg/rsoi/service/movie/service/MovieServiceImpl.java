package ru.oleg.rsoi.service.movie.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oleg.rsoi.dto.MovieRequest;
import ru.oleg.rsoi.dto.RatingRequest;
import ru.oleg.rsoi.service.movie.domain.Movie;
import ru.oleg.rsoi.service.movie.domain.Rating;
import ru.oleg.rsoi.service.movie.repository.MovieRepository;
import ru.oleg.rsoi.service.movie.repository.RatingRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

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
    @Transactional
    public Movie updateById(Integer id, MovieRequest movieRequest) {
        Movie movie = movieRepository.findOne(id);
        if (movie == null) {
            throw new EntityNotFoundException("Movie(" + id + ") not found");
        }
        movie.setName(movieRequest.getName() != null ?  movieRequest.getName() : movie.getName());
        movie.setDescription(movieRequest.getDescription() != null ?
                movieRequest.getDescription() : movie.getDescription());
        return movieRepository.save(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getByName(String name) {
        return movieRepository.findAllByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Movie> get(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Movie save(MovieRequest movieRequest) {
        Movie movie = new Movie()
                .setName(movieRequest.getName())
                .setDescription(movieRequest.getDescription());
        return movieRepository.save(movie);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        movieRepository.delete(id);
    }

    @Override
    @Transactional
    public void rateMovie(RatingRequest ratingRequest) {
        Movie movie = movieRepository.findOne(ratingRequest.getMovieId());
        if (movie == null) {
            throw new EntityNotFoundException("Movie(" + ratingRequest.getMovieId() + ") not found");
        }

        Rating rating = ratingRepository.findByMovieAndUserId(movie, ratingRequest.getUserId());
        if (rating == null) {
            rating = new Rating();
            rating.setMovie(movie);
            rating.setUserId(ratingRequest.getUserId());
        }
        rating.setScore(ratingRequest.getScore());

        ratingRepository.save(rating);
    }
}
