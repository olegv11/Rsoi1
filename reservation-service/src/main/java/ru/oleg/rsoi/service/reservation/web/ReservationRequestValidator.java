package ru.oleg.rsoi.service.reservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.errors.ApiErrorCode;
import ru.oleg.rsoi.service.reservation.domain.Reservation;
import ru.oleg.rsoi.service.reservation.domain.Seat;
import ru.oleg.rsoi.service.reservation.repository.SeatRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservationRequestValidator implements Validator {

    @Autowired
    SeatRepository seatRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return ReservationRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ReservationRequest request = (ReservationRequest)target;

        List<Integer> seatIds = request.getSeatIds();
        for (int i = 0; i < seatIds.size(); i++) {
            Integer seatId = seatIds.get(i);
            Seat seat = seatRepository.findOne(seatId);
            if (seat == null) {
                errors.rejectValue("seatIds["+i+"]", ApiErrorCode.SEAT_NOT_EXISTS.getCode(),
                        "Seat does not exist");
            }
            else if (!seat.isAvailable()) {
                errors.rejectValue("seatIds["+i+"]", ApiErrorCode.SEAT_TAKEN.getCode(),
                        "Seat taken");
            }
        }
    }
}
