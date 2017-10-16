package ru.oleg.rsoi.service.reservation.domain;


import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.SeatType;

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

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private boolean isAvailable;
}

