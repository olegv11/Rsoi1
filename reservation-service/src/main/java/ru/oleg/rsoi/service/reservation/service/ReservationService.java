package ru.oleg.rsoi.service.reservation.service;

import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.service.reservation.domain.Reservation;

import java.util.List;

public interface ReservationService {
    Reservation getById(Integer id);
    List<Reservation> getBySeance(Integer seanceId);
    List<Reservation> getByUser(Integer user_id);
    Reservation makeReservation(ReservationRequest request);
    Reservation bindReservationToBill(Integer reservationId, Integer billId);
    void deleteReservation(Integer id);
    int getPriceOf(Reservation reservation);
}
