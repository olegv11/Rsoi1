package ru.oleg.rsoi.dto;


import lombok.Data;

import java.util.List;

@Data
public class ReservationResponse {
    Integer id;
    Integer seance_id;
    Integer bill_id;
    Integer user_id;
    List<SeatResponse> seats;
}
