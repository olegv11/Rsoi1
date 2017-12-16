package ru.oleg.rsoi.dto.reservation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.reservation.SeatResponse;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {
    Integer id;
    Integer seance_id;
    Integer bill_id;
    Integer user_id;
    List<SeatResponse> seats;
    Integer amount;
}
