package ru.oleg.rsoi.service.reservation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.reservation.ReservationResponse;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seance_id")
    private Seance seance;

    @Column
    private Integer billId;

    @Column
    private Integer userId;

    @Column
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Seat> seats;

    public ReservationResponse toResponse() {
        return new ReservationResponse(id, seance.getId(), billId, userId,
                seats == null ? null : seats.stream().map(Seat::toResponse).collect(Collectors.toList()));
    }
}
