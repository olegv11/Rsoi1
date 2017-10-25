package ru.oleg.rsoi.dto.gateway;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.reservation.SeatResponse;
import ru.oleg.rsoi.dto.reservation.SeatType;

@Data
@Accessors(chain = true)
public class SeatComposite {
    private Integer seatId;
    private SeatType type;
    private Boolean isAvailable;

    public static SeatComposite from(SeatResponse response) {
        return new SeatComposite()
                .setSeatId(response.getId())
                .setType(response.getSeatType())
                .setIsAvailable(response.getIsAvailable());
    }
}
