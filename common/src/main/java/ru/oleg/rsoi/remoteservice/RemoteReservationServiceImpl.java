package ru.oleg.rsoi.remoteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.ReservationResponse;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;
import org.springframework.data.domain.Page;


import java.util.Date;
import java.util.List;

@Component
public class RemoteReservationServiceImpl implements RemoteReservationService {

    private final RemoteRsoiServiceImpl<SeanceRequest, SeanceResponse> remoteSeanceService;
    private final RemoteRsoiServiceImpl<ReservationRequest, ReservationResponse> remoteReservationService;

    @Autowired
    public RemoteReservationServiceImpl(@Value("${urls.services.reservations}") String reservationServiceUrl) {
        remoteSeanceService = new RemoteRsoiServiceImpl<>(reservationServiceUrl,
                SeanceResponse.class, SeanceResponse[].class);

        remoteReservationService = new RemoteRsoiServiceImpl<>(reservationServiceUrl,
                ReservationResponse.class, ReservationResponse[].class);
    }

    @Override
    public SeanceResponse findSeance(int id) {
        return remoteSeanceService.find(id, "/seance/{id}");
    }

    @Override
    public List<SeanceResponse> findSeancesByMovie(int movieId) {
        return remoteSeanceService.findAll(movieId, "/seance?movie={id}");
    }

    @Override
    public SeanceResponse createSeance(int movieId, Date date) {
        SeanceRequest sr = new SeanceRequest(movieId, date);
        return remoteSeanceService.create(sr, "/seance");
    }

    @Override
    public void deleteSeance(int id) {
        remoteSeanceService.delete(id, "/seance/{id}");
    }

    @Override
    public ReservationResponse findReservation(int id) {
        return remoteReservationService.find(id, "/reservation/{id}");
    }

    @Override
    public List<ReservationResponse> findReservationBySeance(int seanceId) {
        return remoteReservationService.findAll(seanceId, "/reservation?seance={id}");
    }

    @Override
    public List<ReservationResponse> getReservationByUser(int userId) {
        return remoteReservationService.findAll(userId, "/reservation?user={id}");
    }

    @Override
    public ReservationResponse createReservation(int seanceId, int userId, List<Integer> seatIds) {
        ReservationRequest rr = new ReservationRequest(seanceId, userId, seatIds);
        return remoteReservationService.create(rr, "/reservation");
    }

    @Override
    public void deleteReservation(int id) {
        remoteReservationService.delete(id, "/reservation/{id}");
    }
}
