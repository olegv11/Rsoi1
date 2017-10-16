package ru.oleg.rsoi.domain.reservations.seance;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.domain.reservations.seat.Seat;
import ru.oleg.rsoi.domain.reservations.reservation.Reservation;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "seance")
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer movie_id;

    @Column
    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Seat> seats;

    @Column
    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Reservation> reservations;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date screenDate;
}
