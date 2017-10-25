package ru.oleg.rsoi.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    Integer id;
    Integer seance_id;
    SeatType seatType;
    Boolean isAvailable;
}
