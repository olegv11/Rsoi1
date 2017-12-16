package ru.oleg.rsoi.dto.gateway;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import ru.oleg.rsoi.dto.movie.MovieResponse;
import ru.oleg.rsoi.dto.reservation.SeanceResponse;
import ru.oleg.rsoi.dto.reservation.SeatResponse;
import ru.oleg.rsoi.dto.reservation.SeatType;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class SeanceComposite {
    private Integer seanceId;
    private Integer movieId;
    private String movieName;

    @DateTimeFormat(pattern="dd/MM/yyyy")
    private Date date;
    List<SeatComposite> seats;

    public static SeanceComposite from(SeanceResponse response, MovieResponse movieResponse) {
        if (!Objects.equals(response.getMovie_id(), movieResponse.getMovieId())) {
            throw new IllegalArgumentException("Movie id should be same!");
        }

        return new SeanceComposite()
                .setSeanceId(response.getSeance_id())
                .setMovieId(response.getMovie_id())
                .setMovieName(movieResponse.getDescription())
                .setDate(response.getDate())
                .setSeats(response.getSeats() == null ? null :
                        response.getSeats().stream().map(SeatComposite::from).collect(Collectors.toList()));
    }
}

