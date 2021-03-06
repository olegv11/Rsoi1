package ru.oleg.rsoi.service.reservation.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;
import ru.oleg.rsoi.service.reservation.domain.Seance;
import ru.oleg.rsoi.service.reservation.service.SeanceService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seance")
public class SeanceRestController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    SeanceService seanceService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SeanceResponse getSeance(@PathVariable Integer id) {
        logger.debug("SEANCE: getting seance " + id);
        return seanceService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<SeanceResponse> getSeancesByMovie(@RequestParam(value = "movie") Integer movieId) {
        logger.debug("SEANCE: getting seance for movie"+movieId);
        return seanceService.getByMovie(movieId)
                .stream().map(Seance::toResponse).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public SeanceResponse createSeance(@Valid @RequestBody SeanceRequest seanceRequest,
                                       HttpServletResponse response) {
        logger.debug("SEANCE: creating seance "+seanceRequest);
        Seance seance = seanceService.createSeance(seanceRequest);
        response.addHeader(HttpHeaders.LOCATION, "/seance/" + seance.getId());
        return seance.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteSeance(@PathVariable Integer id) {
        logger.debug("SEANCE: deleting seance "+id);
        seanceService.deleteSeance(id);
    }

}
