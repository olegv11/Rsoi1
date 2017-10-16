package ru.oleg.rsoi.service.reservation.service;

import ru.oleg.rsoi.dto.SeatType;
import ru.oleg.rsoi.service.reservation.domain.Seat;
import ru.oleg.rsoi.service.reservation.domain.SeatPrice;

import java.util.HashMap;
import java.util.List;

public class ReservationPriceCalculator {

    public ReservationPriceCalculator(List<SeatPrice> seatPrices) {
        prices = new HashMap<>();
        for (SeatPrice price : seatPrices) {
            prices.put(price.getSeatType(), price.getPrice());
        }
    }

    public int calculatePrice(List<Seat> seats) {
        int sum = 0;
        for (Seat seat : seats) {
            sum += prices.get(seat.getSeatType());
        }

        return sum;
    }

    private HashMap<SeatType, Integer> prices;
}
