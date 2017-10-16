package ru.oleg.rsoi.dto;

import lombok.Data;

@Data
public class SeatResponse {
    Integer seat_id;
    Integer seance_id;
    SeatType seatType;
}
