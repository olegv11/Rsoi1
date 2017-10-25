package ru.oleg.rsoi.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SeanceResponse {
    private Integer seance_id;
    private Integer movie_id;
    private Date date;
    List<SeatResponse> seats;
}
