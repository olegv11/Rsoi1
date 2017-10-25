package ru.oleg.rsoi.remoteservice;


import ru.oleg.rsoi.dto.reservation.ReservationResponse;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;

import java.util.Date;
import java.util.List;

public interface RemoteReservationService {
    SeanceResponse findSeance(int id);
    List<SeanceResponse> findSeancesByMovie(int movieId);
    SeanceResponse createSeance(int movieId, Date date);
    void deleteSeance(int id);

    ReservationResponse findReservation(int id);
    List<ReservationResponse> findReservationBySeance(int seanceId);
    List<ReservationResponse> getReservationByUser(int userId);
    ReservationResponse createReservation(int seanceId, int userId, List<Integer> seatIds);
    void deleteReservation(int id);
}
