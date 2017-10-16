package ru.oleg.rsoi.domain.reservations.reservation;

import lombok.Data;
import ru.oleg.rsoi.domain.reservations.seance.Seance;

import javax.persistence.*;

@Data
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ManyToOne
    @JoinColumn(name = "seance_id")
    private Seance seance;

    @Column
    private Integer bill_id;

    @Column
    private Integer user_id;
}
