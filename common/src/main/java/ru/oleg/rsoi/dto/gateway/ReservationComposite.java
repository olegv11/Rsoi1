package ru.oleg.rsoi.dto.gateway;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.ReservationResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class ReservationComposite {
    private Integer reservationId;
    private Integer seanceId;
    private Integer billId;
    private Integer amount;
    List<SeatComposite> seats;

    public static ReservationComposite from(ReservationResponse reservationResponse, BillResponse billResponse) {
        if (!Objects.equals(reservationResponse.getBill_id(), billResponse.getBillId())) {
            throw new IllegalArgumentException("Bill Ids should be same");
        }
        return new ReservationComposite()
                .setReservationId(reservationResponse.getId())
                .setSeanceId(reservationResponse.getSeance_id())
                .setBillId(reservationResponse.getBill_id())
                .setAmount(billResponse.getAmount())
                .setSeats(reservationResponse.getSeats() == null ? null :
                        reservationResponse.getSeats().stream().map(SeatComposite::from).collect(Collectors.toList()));
    }

    public static ReservationComposite from(ReservationResponse reservationResponse) {
        return new ReservationComposite()
                .setReservationId(reservationResponse.getId())
                .setSeanceId(reservationResponse.getSeance_id())
                .setSeats(reservationResponse.getSeats() == null ? null :
                        reservationResponse.getSeats().stream().map(SeatComposite::from).collect(Collectors.toList()))
                .setBillId(null)
                .setAmount(null);
    }
}
