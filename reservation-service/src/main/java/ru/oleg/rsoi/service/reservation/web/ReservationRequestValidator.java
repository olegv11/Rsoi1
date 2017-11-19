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

        for (Integer seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findOne(seatId);
            if (seat == null) {
                errors.rejectValue("seatIds", seatId.toString(), ApiErrorCode.SEAT_NOT_EXISTS.getCode());
                continue;
            }
            if (!seat.isAvailable()) {
                errors.rejectValue("seatIds", seatId.toString(), ApiErrorCode.SEAT_TAKEN.getCode());
            }
        }

    }
}
