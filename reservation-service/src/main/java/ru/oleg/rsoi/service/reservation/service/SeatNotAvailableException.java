package ru.oleg.rsoi.service.reservation.service;

public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException() {
        super();
    }

    public SeatNotAvailableException(String message) {
        super(message);
    }
}
