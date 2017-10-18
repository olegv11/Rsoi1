package ru.oleg.rsoi.service.reservation.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.SeanceResponse;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "seance")
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer movieId;

    @Column
    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<Seat> seats;

    @Column
    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Reservation> reservations;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date screenDate;

    public SeanceResponse toResponse() {
        return new SeanceResponse(id, movieId,
                seats.stream().map(Seat::toResponse).collect(Collectors.toList()));
    }
}
