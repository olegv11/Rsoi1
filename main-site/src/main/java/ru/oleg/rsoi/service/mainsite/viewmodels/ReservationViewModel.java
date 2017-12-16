package ru.oleg.rsoi.service.mainsite.viewmodels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oleg.rsoi.dto.gateway.SeanceComposite;
import ru.oleg.rsoi.dto.gateway.SeatComposite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationViewModel {
    private Integer seanceId;
    private Integer userId;

    Integer[] seatIds;
}
