package ru.oleg.rsoi.service.mainsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.gateway.MovieComposite;
import ru.oleg.rsoi.dto.gateway.ReservationComposite;
import ru.oleg.rsoi.dto.gateway.SeanceComposite;
import ru.oleg.rsoi.dto.gateway.SeatComposite;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.errors.ApiErrorCode;
import ru.oleg.rsoi.errors.ApiErrorView;
import ru.oleg.rsoi.errors.ApiErrorViewException;
import ru.oleg.rsoi.errors.ApiFieldError;
import ru.oleg.rsoi.remoteservice.PageWrapper;
import ru.oleg.rsoi.remoteservice.RemoteGatewayService;
import ru.oleg.rsoi.remoteservice.RemoteServiceException;
import ru.oleg.rsoi.service.mainsite.viewmodels.ReservationViewModel;
import ru.oleg.rsoi.service.mainsite.viewmodels.SeanceViewModel;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SeanceController {
    @Autowired
    RemoteGatewayService gatewayService;

    @GetMapping(value ="/seance/{id}")
    public String seance(@PathVariable Integer id, Model model) {
        SeanceComposite seance = gatewayService.getSeance(id);
        seance.getSeats().sort(new Comparator<SeatComposite>() {
            @Override
            public int compare(SeatComposite t0, SeatComposite t1) {
                return t0.getSeatId() - t1.getSeatId();
            }
        });

        ReservationViewModel reservation = new ReservationViewModel(id, 3, null);

        model.addAttribute("reservation", reservation);
        model.addAttribute("seance", seance);
        return "seance/seance";
    }

    @GetMapping(value = "/movie/{id}/seance/create")
    public String create(@PathVariable Integer id, Model model) {
        MovieComposite movie = gatewayService.getMovie(id);
        SeanceViewModel seance = new SeanceViewModel();
        seance.setMovieId(movie.getMovieId());
        seance.setDate("01/02/1998");

        model.addAttribute("seance", seance);
        return "seance/seanceForm";
    }

    @PostMapping(value = "/movie/{id}/seance")
    public String create(@Valid @ModelAttribute("seance") SeanceViewModel seance, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("seance", seance);
            return "seance/seanceForm";
        }

        SeanceRequest request = new SeanceRequest()
                .setMovieId(seance.getMovieId());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d;

        try {
             d = sdf.parse(seance.getDate());
        } catch (ParseException e) {
            d = new Date(Instant.now().getEpochSecond());
        }
        request.setScreenDate(d);

        SeanceComposite s = gatewayService.createSeance(request);

        return "redirect:/seance/" + s.getSeanceId();
    }

    /*
    @PostMapping(value = "/seance/")
    public String edit(SeanceRequest seance) {
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
*/

    @PostMapping(value = "/seance/{id}/delete")
    public String delete(@PathVariable Integer id, SeanceComposite seance) {
        try {
            gatewayService.deleteSeance(id);
        }
        catch (RemoteServiceException ex) {
            if (ex.getStatus() == HttpStatus.NOT_FOUND) {
                return "404";
            } else {
                throw ex;
            }
        }

        return "redirect:/movie/"+seance.getMovieId();
    }


    @PostMapping(value="/seance/{id}")
    public String order(ReservationViewModel reservation, @PathVariable Integer id,
                        @RequestParam(value = "seats", required = false) int[] seatIds, Model model) {
        ReservationRequest request = new ReservationRequest(reservation.getSeanceId(),
                reservation.getUserId(), Arrays.stream(seatIds).boxed().collect(Collectors.toList()));

        ReservationComposite returnedReservation;
        try {
            returnedReservation = gatewayService.createReservation(request);
        }
        catch (ApiErrorViewException ex) {
            ApiErrorView view = ex.getView();
            List<Integer> takenSeats = view.getFieldErrors().stream()
                    .filter(x -> x.getCode().equals(ApiErrorCode.SEAT_TAKEN.getCode()))
                    .map(x -> (Integer)x.getRejectedValue())
                    .collect(Collectors.toList());

            if (takenSeats != null) {
                model.addAttribute("takenSeats", takenSeats);
            }

            return seance(reservation.getSeanceId(), model);
        }


        return "redirect:/reservation/" + returnedReservation.getReservationId();
    }

    @GetMapping(value="/reservation/{id}")
    public String reservation(@PathVariable Integer id, Model model) {
        ReservationComposite returnedReservation = gatewayService.getReservation(id);

        model.addAttribute("reservation", returnedReservation);
        return "/seance/reservation";
    }
}
