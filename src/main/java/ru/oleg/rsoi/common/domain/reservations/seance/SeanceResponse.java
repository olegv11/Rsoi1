package ru.oleg.rsoi.domain.reservations.seance;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.domain.reservations.seat.SeatResponse;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class SeanceResponse {
    private Integer seance_id;
    private Integer movie_id;
    List<SeatResponse> seats;

    public SeanceResponse(Seance seance) {
        this.seance_id = seance.getId();
        this.movie_id = seance.getMovie_id();
        this.seats = seance.getSeats().stream().map(SeatResponse::new).collect(Collectors.toList());
    }
}
