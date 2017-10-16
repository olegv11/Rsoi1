package ru.oleg.rsoi.domain.reservations.seat;

import lombok.Data;

@Data
public class SeatResponse {
    Integer seat_id;
    Integer seance_id;
    SeatType seatType;

    public SeatResponse(Seat seat) {
        this.seat_id = seat.getId();
        this.seance_id = seat.getSeance().getId();
        this.seatType = seat.getSeatType();
    }
}
