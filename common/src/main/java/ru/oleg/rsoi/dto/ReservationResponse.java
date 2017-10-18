package ru.oleg.rsoi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReservationResponse {
    Integer id;
    Integer seance_id;
    Integer bill_id;
    Integer user_id;
    List<SeatResponse> seats;
}
