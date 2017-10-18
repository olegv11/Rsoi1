package ru.oleg.rsoi.remoteservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.oleg.rsoi.dto.MovieResponse;
import ru.oleg.rsoi.dto.ReservationResponse;
import ru.oleg.rsoi.dto.SeanceRequest;
import ru.oleg.rsoi.dto.SeanceResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemoteReservationServiceImpl implements RemoteReservationService {
    @Value("{urls.services.reservations}")
    String reservationUrl;

    private final RestTemplate rt = new RestTemplate();

    @Override
    public SeanceResponse getSeance(int id) {
        ResponseEntity<SeanceResponse> response =
                rt.getForEntity(reservationUrl + "/seance/{id}", SeanceResponse.class, id);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        return null;
    }

    @Override
    public List<SeanceResponse> getSeances(int movieId) {
        ResponseEntity<SeanceResponse[]> response
                = rt.getForEntity(reservationUrl + "/seance?movie={id}",
                SeanceResponse[].class, movieId);

        if (response.getStatusCode() == HttpStatus.OK) {
            return Arrays.asList(response.getBody());
        }

        return null;
    }

    @Override
    public SeanceResponse createSeance(int movieId, Date date) {
        SeanceRequest sr = new SeanceRequest(movieId, date);

        //ResponseEntity<MovieResponse> re = rt.postForEntity()
        return null;
    }

    @Override
    public void deleteSeance(int id) {

    }

    @Override
    public ReservationResponse getReservation(int id) {
        return null;
    }

    @Override
    public List<ReservationResponse> getReservationByUser(int userId) {
        return null;
    }

    @Override
    public ReservationResponse createReservation(int seanceId, int userId, List<Integer> seatIds) {
        return null;
    }
}
