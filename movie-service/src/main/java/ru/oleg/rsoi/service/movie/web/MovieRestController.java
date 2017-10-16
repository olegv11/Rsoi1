package ru.oleg.rsoi.service.movie.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.MovieRequest;
import ru.oleg.rsoi.dto.MovieResponse;
import ru.oleg.rsoi.dto.RatingRequest;
import ru.oleg.rsoi.service.movie.domain.Movie;
import ru.oleg.rsoi.service.movie.service.MovieService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/movie")
public class MovieRestController {

    @Autowired
    MovieService movieService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MovieResponse getMovie(@PathVariable Integer id) {
        return movieService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<MovieResponse> getAllMovies(Pageable page) {
        return movieService.get(page).map(Movie::toResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public MovieResponse createMovie(@RequestBody MovieRequest movieRequest, HttpServletResponse response) {
        Movie movie = movieService.save(movieRequest);
        response.addHeader(HttpHeaders.LOCATION, "/movie/" + movie.getId());
        return movie.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteMovie(@PathVariable Integer id) {
        movieService.deleteById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public MovieResponse updateMovie(@PathVariable Integer id, @RequestBody MovieRequest movieRequest) {
        return movieService.updateById(id, movieRequest).toResponse();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/rate", method = RequestMethod.POST)
    public void rateMovie(@RequestBody RatingRequest ratingRequest, HttpServletResponse response) {
        movieService.rateMovie(ratingRequest);
    }




}
