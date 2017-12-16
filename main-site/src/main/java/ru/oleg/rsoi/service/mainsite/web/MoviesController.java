package ru.oleg.rsoi.service.mainsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.gateway.MovieComposite;
import ru.oleg.rsoi.dto.gateway.SeanceComposite;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.remoteservice.PageWrapper;
import ru.oleg.rsoi.remoteservice.RemoteGatewayService;
import ru.oleg.rsoi.remoteservice.RemoteServiceException;
import ru.oleg.rsoi.remoteservice.RestResponsePage;

import javax.validation.Valid;
import java.util.List;

@Controller
public class MoviesController {

    @Autowired
    RemoteGatewayService gatewayService;

    @GetMapping(value = "/movies")
    public String movies(Model model, Pageable pageable) {
        Page<MovieComposite> movies = gatewayService.getMovies(pageable);
        PageWrapper<MovieComposite> page = new PageWrapper<>(movies, "/movies");
        model.addAttribute("movies", page.getContent());
        model.addAttribute("page", page);
        return "movie/movies";
    }

    @PostMapping(value = "/movie/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {
        try {
            gatewayService.deleteMovie(id);
        }
        catch (RemoteServiceException ex) {
            if (ex.getStatus() == HttpStatus.NOT_FOUND) {
                return "404";
            } else {
                throw ex;
            }
        }

        return "redirect:/movies";
    }

    @GetMapping(value ="/movie/{id}")
    public String movie(@PathVariable Integer id, Model model) {
        MovieComposite movie = gatewayService.getMovie(id);
        List<SeanceComposite> seances = gatewayService.getSeances(id);

        model.addAttribute("movie", movie);
        model.addAttribute("seances", seances);
        return "movie/movie";
    }

    @GetMapping(value="/movie/create")
    public String createMovie(Model model) {
        model.addAttribute("movie", new MovieComposite());
        model.addAttribute("isUpdate", false);

        return "movie/movieForm";
    }

    @PostMapping(value="/createMovie")
    public String create(@Valid @ModelAttribute("movie") MovieComposite movie, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isUpdate", false);
            model.addAttribute("movie", movie);
            return "movie/movieForm";
        }

        MovieRequest request = new MovieRequest(movie.getName(), movie.getDescription());
        MovieComposite created = gatewayService.createMovie(request);

        return "redirect:/movie/" + created.getMovieId();
    }

    @PostMapping(value = "/movie/")
    public String edit(@Valid @ModelAttribute("movie") MovieComposite movie, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isUpdate", true);
            model.addAttribute("movie", movie);
            return "movie/movieForm";
        }

        MovieRequest request = new MovieRequest(movie.getName(), movie.getDescription());
        gatewayService.updateMovie(movie.getMovieId(), request);

        return "redirect:/movie/"+movie.getMovieId();
    }

    @GetMapping(value = "/movie/{id}/edit")
    public String editMovie(@PathVariable Integer id, Model model) {
        MovieComposite movie = gatewayService.getMovie(id);

        model.addAttribute("movie", movie);
        model.addAttribute("isUpdate", true);
        return "/movie/movieForm";
    }


}
