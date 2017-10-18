package ru.oleg.rsoi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatResponse {
    Integer seat_id;
    Integer seance_id;
    SeatType seatType;
    boolean isAvailable;
}
