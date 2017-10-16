package ru.oleg.rsoi.domain.reservations.seat;


import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.domain.reservations.seance.Seance;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "seance_id")
    private Seance seance;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;
}

