package ru.oleg.rsoi.service.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.remoteservice.RemoteGatewayService;
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
import java.util.stream.Collectors;

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
    RemoteGatewayService gatewayService;

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

        Reservation reservation = new Reservation()
                .setUserId(request.getUserId())
                .setSeance(seance)
                .setSeats(seats);

        seats.forEach(x -> x.setAvailable(false));
        seats.forEach(x -> x.setReservation(reservation));

        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation bindReservationToBill(Integer reservationId, Integer billId) {
        Reservation reservation = reservationRepository.findOne(reservationId);

        if (reservation == null) {
            throw new EntityNotFoundException("Reservation("+reservationId+") does not exist");
        }

        reservation.setBillId(billId);
        reservationRepository.save(reservation);

        return reservation;
    }

    @Override
    @Transactional
    public void deleteReservation(Integer id) {
        List<Seat> seats = seatRepository.findAll().stream()
                .filter(x -> x.getReservation() != null && x.getReservation().getId().equals(id)).collect(Collectors.toList());

        seats.forEach(x -> x.setReservation(null));
        seatRepository.save(seats);

        if (reservationRepository.exists(id)) {
            reservationRepository.delete(id);
        }
    }

    @Override
    public int getPriceOf(Reservation reservation) {
        ReservationPriceCalculator calculator = new ReservationPriceCalculator(seatPriceRepository.findAll());
        return calculator.calculatePrice(reservation.getSeats());
    }
}
