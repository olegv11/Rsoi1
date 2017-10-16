package ru.oleg.rsoi.service.reservation.domain;

import lombok.Data;
import ru.oleg.rsoi.dto.SeatType;

import javax.persistence.*;

@Data
@Entity
@Table(name = "seat_price")
public class SeatPrice {
    @Id
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column
    private Integer price;
}
