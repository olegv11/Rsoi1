package ru.oleg.rsoi.service.reservation.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.oleg.rsoi.dto.reservation.ReservationRequest;
import ru.oleg.rsoi.dto.reservation.ReservationResponse;
import ru.oleg.rsoi.dto.reservation.SeatType;
import ru.oleg.rsoi.remoteservice.RemoteServiceException;
import ru.oleg.rsoi.service.reservation.domain.Reservation;
import ru.oleg.rsoi.service.reservation.domain.Seance;
import ru.oleg.rsoi.service.reservation.domain.Seat;
import ru.oleg.rsoi.service.reservation.service.ReservationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ReservationRestController.class)
public class ReservationRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    ReservationService service;

    @Test
    public void getReservation() throws Exception {
        // Arrange
        Seance seance = new Seance();
        seance.setId(1000);

        ArrayList<Seat> reservedSeats = new ArrayList<>();
        reservedSeats.add(new Seat());
        reservedSeats.get(0).setId(0).setSeatType(SeatType.Normal).setAvailable(true).setSeance(seance);
        reservedSeats.add(new Seat());
        reservedSeats.get(1).setId(5).setSeatType(SeatType.Elite).setAvailable(true).setSeance(seance);

        Reservation reservation = new Reservation();
        reservation.setId(100).setBillId(2).setUserId(500).setSeance(seance).setSeats(reservedSeats);

        given(service.getById(100)).willReturn(reservation);

        // Act
        MvcResult result = mvc.perform(get("/reservation/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ReservationResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                ReservationResponse.class);

        // Assert
        assertThat(reservation.toResponse()).isEqualTo(response);
    }

    @Test
    public void getReservationByUser() throws Exception {
        // Arrange
        Seance seance1 = new Seance();
        seance1.setId(1000);

        Seance seance2 = new Seance();
        seance2.setId(2000);

        ArrayList<Seat> reservedSeats1 = new ArrayList<>();
        reservedSeats1.add(new Seat());
        reservedSeats1.get(0).setId(0).setSeatType(SeatType.Normal).setAvailable(true).setSeance(seance1);

        ArrayList<Seat> reservedSeats2 = new ArrayList<>();
        reservedSeats2.add(new Seat());
        reservedSeats2.get(0).setId(5).setSeatType(SeatType.Elite).setAvailable(true).setSeance(seance2);

        Reservation reservation1 = new Reservation();
        reservation1.setId(100).setBillId(2).setUserId(500).setSeance(seance1).setSeats(reservedSeats1);

        Reservation reservation2 = new Reservation();
        reservation2.setId(101).setBillId(3).setUserId(500).setSeance(seance2).setSeats(reservedSeats2);

        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);

        given(service.getByUser(500)).willReturn(reservations);

        // Act
        MvcResult result = mvc.perform(get("/reservation?user=500").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ReservationResponse[] responses = mapper.readValue(result.getResponse().getContentAsString(),
                ReservationResponse[].class);

        // Assert
        assertThat(reservations.stream().map(Reservation::toResponse).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(responses);
    }

    @Test
    public void getReservationBySeance() throws Exception {
        // Arrange
        Seance seance = new Seance().setId(100);

        Reservation reservation1 = new Reservation()
                .setId(1).setSeance(seance);
        Reservation reservation2 = new Reservation()
                .setId(2).setSeance(seance);

        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);

        given(service.getBySeance(100)).willReturn(reservations);

        // Act
        MvcResult result = mvc.perform(get("/reservation?seance=100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ReservationResponse[] responses = mapper.readValue(result.getResponse().getContentAsString(),
                ReservationResponse[].class);

        // Assert
        assertThat(reservations.stream().map(Reservation::toResponse).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(responses);
    }

    @Test
    public void createReservation() throws Exception {
        // Arrange
        Seance seance = new Seance().setId(1);
        ArrayList<Seat> reservedSeats = new ArrayList<>();
        reservedSeats.add(new Seat());
        reservedSeats.get(0).setId(0).setSeatType(SeatType.Normal).setAvailable(true).setSeance(seance);
        reservedSeats.add(new Seat());
        reservedSeats.get(1).setId(5).setSeatType(SeatType.Elite).setAvailable(true).setSeance(seance);

        ReservationRequest request = new ReservationRequest(1000, 500,
                reservedSeats.stream().map(Seat::getId).collect(Collectors.toList()));
        Reservation reservation = new Reservation();
        reservation.setId(51).setSeance(seance).setSeats(reservedSeats).setUserId(1);

        given(service.makeReservation(request)).willReturn(reservation);

        // Act
        MvcResult result = mvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/reservation/51"))
                .andReturn();

        // Assert
        assertThat(mapper.readValue(result.getResponse().getContentAsString(), ReservationResponse.class))
                .isEqualTo(reservation.toResponse());

    }

    @Test
    public void deleteReservation() throws Exception {
        mvc.perform(delete("/reservation/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void generalError() throws Exception {
        // Arrange
        given(service.getById(anyInt())).willThrow(new RuntimeException("Test"));

        // Act/assert
        mvc.perform(get("/reservation/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void remoteError() throws Exception {
        given(service.getById(anyInt())).willThrow(new RemoteServiceException("RemoteExceptionTest"));

        // Act/assert
        mvc.perform(get("/reservation/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}