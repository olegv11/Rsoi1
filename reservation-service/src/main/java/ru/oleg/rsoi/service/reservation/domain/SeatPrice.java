package ru.oleg.rsoi.service.reservation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.oleg.rsoi.dto.reservation.SeatType;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@Table(name = "seat_price")
public class SeatPrice {
    @Id
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column
    private Integer price;
}
