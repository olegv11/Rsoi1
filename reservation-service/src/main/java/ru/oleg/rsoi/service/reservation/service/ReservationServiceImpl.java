package ru.oleg.rsoi.service.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.remoteservice.RemotePaymentService;
import ru.oleg.rsoi.service.reservation.domain.Reservation;
import ru.oleg.rsoi.service.reservation.domain.Seance;
import ru.oleg.rsoi.service.reservation.domain.Seat;
import ru.oleg.rsoi.service.reservation.repository.ReservationRepository;
import ru.oleg.rsoi.service.reservation.repository.SeanceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatPriceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SeanceRepository seanceRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    SeatPriceRepository seatPriceRepository;

    @Autowired
    RemotePaymentService paymentService;

    @Override
    @Transactional(readOnly = true)
    public Reservation getById(Integer id) {
        Reservation reservation = reservationRepository.findOne(id);
        if (reservation == null) {
            throw new EntityNotFoundException("Reservation("+id+") not found");
        }
        return reservation;
    }

    @Override
    public List<Reservation> getBySeance(Integer seanceId) {
        Seance seance = seanceRepository.findOne(seanceId);
        return reservationRepository.findAllBySeance(seance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getByUser(Integer user_id) {
        return reservationRepository.findAllByUserId(user_id);
    }

    @Override
    @Transactional
    public Reservation makeReservation(ReservationRequest request) {
        Seance seance = seanceRepository.findOne(request.getSeanceId());
        if (seance == null) {
            throw new EntityNotFoundException("Seance("+request.getSeanceId()+") not found");
        }

        List<Seat> seats = seatRepository.findAll(request.getSeatIds());
        if (seats.size() != request.getSeatIds().size()) {
            throw new EntityNotFoundException("Not all seats exist!");
        }

        if (seats.stream().anyMatch(x -> !x.isAvailable())) {
            throw new SeatNotAvailableException("Some seat is not available");
        }

        ReservationPriceCalculator calculator = new ReservationPriceCalculator(seatPriceRepository.findAll());
        int price = calculator.calculatePrice(seats);

        BillResponse bill = paymentService.createBill(price);

        Reservation reservation = new Reservation()
                .setUserId(request.getUserId())
                .setSeance(seance)
                .setSeats(seats)
                .setBillId(bill.getBillId());
        seats.forEach(x -> x.setAvailable(false));

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Integer id) {
        reservationRepository.delete(id);
    }
}
