package ru.oleg.rsoi.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotNull
    Integer seanceId;
    @NotNull
    Integer userId;
    List<Integer> seatIds;
}
