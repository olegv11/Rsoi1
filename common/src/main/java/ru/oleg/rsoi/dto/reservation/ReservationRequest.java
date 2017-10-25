package ru.oleg.rsoi.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    Integer seanceId;
    Integer userId;
    List<Integer> seatIds;
}
