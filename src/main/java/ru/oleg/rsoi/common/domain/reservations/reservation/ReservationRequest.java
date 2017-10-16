package ru.oleg.rsoi.domain.reservations.reservation;

import lombok.Data;

import java.util.List;

@Data
public class ReservationRequest {
    Integer seance_id;
    Integer user_id;
    List<Integer> seats;
}
