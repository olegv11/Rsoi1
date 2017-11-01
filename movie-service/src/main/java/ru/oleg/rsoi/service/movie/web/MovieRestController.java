package ru.oleg.rsoi.service.movie.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.movie.MovieResponse;
import ru.oleg.rsoi.dto.movie.RatingRequest;
import ru.oleg.rsoi.service.movie.domain.Movie;
import ru.oleg.rsoi.service.movie.service.MovieService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/movie")
public class MovieRestController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    MovieService movieService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MovieResponse getMovie(@PathVariable Integer id) {
        logger.debug("MOVIE: getting movie " + id);
        return movieService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<MovieResponse> getAllMovies(Pageable page) {
        logger.debug("MOVIE: getting movies with page " + page);
        return movieService.get(page).map(Movie::toResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public MovieResponse createMovie(@RequestBody MovieRequest movieRequest, HttpServletResponse response) {
        logger.debug("MOVIE: creating movie with request " + movieRequest);
        Movie movie = movieService.save(movieRequest);
        response.addHeader(HttpHeaders.LOCATION, "/movie/" + movie.getId());
        return movie.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteMovie(@PathVariable Integer id) {
        logger.debug("MOVIE: deleting movie "+id);
        movieService.deleteById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public MovieResponse updateMovie(@PathVariable Integer id, @RequestBody MovieRequest movieRequest) {
        logger.debug("MOVIE: updating movie " + id + " with request " + movieRequest);
        return movieService.updateById(id, movieRequest).toResponse();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/rate", method = RequestMethod.POST)
    public void rateMovie(@RequestBody RatingRequest ratingRequest, HttpServletResponse response) {
        logger.debug("MOVIE: rating movie " + ratingRequest);
        movieService.rateMovie(ratingRequest);
    }




}
