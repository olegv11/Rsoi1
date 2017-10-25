package ru.oleg.rsoi.service.reservation.domain;


import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.reservation.SeatResponse;
import ru.oleg.rsoi.dto.reservation.SeatType;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "seat")
@ToString(exclude = "seance")
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

    public SeatResponse toResponse() {
        return new SeatResponse(id, seance.getId(), seatType, isAvailable);
    }
}

