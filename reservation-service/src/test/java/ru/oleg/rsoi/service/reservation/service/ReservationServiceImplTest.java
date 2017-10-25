package ru.oleg.rsoi.service.reservation.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.SeatType;
import ru.oleg.rsoi.remoteservice.RemotePaymentService;
import ru.oleg.rsoi.service.reservation.domain.Reservation;
import ru.oleg.rsoi.service.reservation.domain.Seance;
import ru.oleg.rsoi.service.reservation.domain.Seat;
import ru.oleg.rsoi.service.reservation.domain.SeatPrice;
import ru.oleg.rsoi.service.reservation.repository.ReservationRepository;
import ru.oleg.rsoi.service.reservation.repository.SeanceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatPriceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReservationServiceImplTest {
    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private SeanceRepository seanceRepository;

    @MockBean
    SeatRepository seatRepository;

    @MockBean
    SeatPriceRepository seatPriceRepository;

    @MockBean
    RemotePaymentService paymentService;

    @Autowired
    ReservationServiceImpl service;

    private Seance seance;
    private ArrayList<Seat> reservedSeats;
    private List<Integer> seatIds;
    private Reservation reservation;
    private ReservationRequest request;
    private Seat unavailableSeat;

    public ReservationServiceImplTest() {
        seance = new Seance().setId(1);

        reservedSeats = new ArrayList<>();
        reservedSeats.add(new Seat());
        reservedSeats.get(0).setId(0).setSeatType(SeatType.Normal).setAvailable(true).setSeance(seance);
        reservedSeats.add(new Seat());
        reservedSeats.get(1).setId(5).setSeatType(SeatType.Elite).setAvailable(true).setSeance(seance);

        unavailableSeat = new Seat();
        unavailableSeat.setId(7).setSeatType(SeatType.Elite).setAvailable(false).setSeance(seance);

        seatIds = reservedSeats.stream().map(Seat::getId).collect(Collectors.toList());

        reservation = new Reservation()
                .setId(10)
                .setSeance(seance)
                .setUserId(200)
                .setBillId(5000)
                .setSeats(reservedSeats);

        request = new ReservationRequest(seance.getId(),200, seatIds);
    }

    @Test
    public void getById() throws Exception {
        // Arrange
        given(reservationRepository.findOne(10)).willReturn(reservation);

        // Act
        Reservation result = service.getById(10);

        // Arrange
        assertThat(result).isEqualTo(reservation);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getByIdThrowsOnNonexistentReservation() throws Exception {
        // Arrange
        given(reservationRepository.findOne(anyInt())).willReturn(null);

        // Act/Assert
        service.getById(1);
    }

    @Test(expected = EntityNotFoundException.class)
    public void makingReservationWithoutSeanceThrows() throws Exception {
        // Arrange
        given(seanceRepository.findOne(request.getSeanceId())).willReturn(null);

        // Act/Assert
        service.makeReservation(request);
    }

    @Test(expected = EntityNotFoundException.class)
    public void makingReservationWithNonexistentSeatsThrows() throws Exception {
        // Arrange
        given(seanceRepository.findOne(request.getSeanceId())).willReturn(seance);
        given(seatRepository.findAll(seatIds)).willReturn(new ArrayList<Seat>());

        // Act/assert
        service.makeReservation(request);
    }

    @Test(expected = SeatNotAvailableException.class)
    public void makingReservationWithUnavailableSeatThrows() throws Exception {
        // Arrange
        ArrayList<Seat> newSeats = new ArrayList<>(reservedSeats);
        newSeats.add(unavailableSeat);
        List<Integer> newSeatIds = newSeats.stream().map(Seat::getId).collect(Collectors.toList());

        given(seanceRepository.findOne(request.getSeanceId())).willReturn(seance);
        given(seatRepository.findAll(newSeatIds)).willReturn(newSeats);

        ReservationRequest requestWithUnavailableSeat = new ReservationRequest(seance.getId(),200, newSeatIds);

        // Act/assert
        service.makeReservation(requestWithUnavailableSeat);
    }

    @Test
    public void makeReservation() {
        // Arrange
        given(seanceRepository.findOne(request.getSeanceId())).willReturn(seance);
        given(seatRepository.findAll(seatIds)).willReturn(reservedSeats);

        List<SeatPrice> prices = new ArrayList<>();
        prices.add(new SeatPrice(SeatType.Normal, 10));
        prices.add(new SeatPrice(SeatType.Elite, 100));
        given(seatPriceRepository.findAll()).willReturn(prices);

        BillResponse billResponse = new BillResponse(5000, 110);
        given(paymentService.createBill(anyInt())).willReturn(billResponse);

        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);

        // Act
        Reservation result = service.makeReservation(request);

        // Assert
        assertThat(reservation).isEqualTo(result);
    }

    @Test
    public void deleteReservation() throws Exception {
        service.deleteReservation(10);

        verify(reservationRepository, times(1)).delete(10);
    }
}