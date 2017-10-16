package ru.oleg.rsoi.domain.reservations.seat;

import lombok.Data;

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
