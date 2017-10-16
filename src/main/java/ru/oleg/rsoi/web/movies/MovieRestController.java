package ru.oleg.rsoi.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.oleg.rsoi.service.movies.MovieService;

@RestController
@RequestMapping("/movie")
public class MovieRestController {

    @Autowired
    MovieService movieService;

    
}
