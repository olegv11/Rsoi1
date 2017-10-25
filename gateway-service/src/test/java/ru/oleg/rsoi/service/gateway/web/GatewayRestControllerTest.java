package ru.oleg.rsoi.service.gateway.web;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.oleg.rsoi.dto.gateway.MovieComposite;
import ru.oleg.rsoi.dto.gateway.ReservationComposite;
import ru.oleg.rsoi.dto.gateway.SeanceComposite;
import ru.oleg.rsoi.dto.movie.MovieRequest;
import ru.oleg.rsoi.dto.movie.MovieResponse;
import ru.oleg.rsoi.dto.movie.RatingRequest;
import ru.oleg.rsoi.dto.payment.BillResponse;
import ru.oleg.rsoi.dto.reservation.*;
import ru.oleg.rsoi.remoteservice.RemoteMovieService;
import ru.oleg.rsoi.remoteservice.RemotePaymentService;
import ru.oleg.rsoi.remoteservice.RemoteReservationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(GatewayRestController.class)
public class GatewayRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RemoteMovieService movieService;

    @MockBean
    private RemotePaymentService paymentService;

    @MockBean
    private RemoteReservationService reservationService;

    @Test
    public void getMovies() throws Exception {
        // Arrange
        List<MovieResponse> list = new ArrayList<>();
        list.add(new MovieResponse()
                .setMovieId(100)
                .setAverageRating(21.0)
                .setDescription("It was ok, 9.6/10")
                .setName("Some movie"));

        list.add(new MovieResponse()
                .setMovieId(105)
                .setAverageRating(99.0)
                .setDescription("Description")
                .setName("Who cares"));
        PageRequest pr = new PageRequest(1,2);
        PageImpl<MovieResponse> result = new PageImpl<>(list, pr, 200);

        given(movieService.movies(pr)).willReturn(result);

        // Act/Assert
        mvc.perform(get("/movie?page=1&size=2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(200)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.content.size()", is(2)))
                .andExpect(jsonPath("$.content[0].movieId", is(100)))
                .andExpect(jsonPath("$.content[1].movieId", is(105)));
    }

    @Test
    public void getMovieById() throws Exception {
        // Arrange
        MovieResponse movie = new MovieResponse()
                .setMovieId(106)
                .setName("Name")
                .setDescription("Some description")
                .setAverageRating(1.1);

        given(movieService.findMovie(106)).willReturn(movie);

        // Act
        MvcResult result = mvc.perform(get("/movie/106").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        MovieComposite composite = mapper.readValue(result.getResponse().getContentAsString(), MovieComposite.class);

        // Assert
        assertThat(composite).isEqualTo(MovieComposite.from(movie));
    }

    @Test
    public void createMovie() throws Exception {
        // Arrange
        MovieRequest request = new MovieRequest()
                .setName("Name")
                .setDescription("Some description");

        MovieResponse movie = new MovieResponse()
                .setMovieId(106)
                .setName(request.getName())
                .setDescription(request.getDescription());

        given(movieService.createMovie("Name", "Some description")).willReturn(movie);

        // Act
        MvcResult result = mvc.perform(post("/movie").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        MovieComposite composite = mapper.readValue(result.getResponse().getContentAsString(), MovieComposite.class);

        // Assert
        assertThat(composite).isEqualTo(MovieComposite.from(movie));
    }

    @Test
    public void updateMovie() throws Exception {
        // Arrange
        MovieRequest request = new MovieRequest()
                .setName("Name")
                .setDescription("Some description");

        // Act
        mvc.perform(patch("/movie/106").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        verify(movieService, times(1))
                .updateMovie(106, request.getName(), request.getDescription());
    }

    @Test
    public void deleteMovie() throws Exception {
        // Arrange
        List<SeanceResponse> seances = new ArrayList<>();
        seances.add(new SeanceResponse()
            .setMovie_id(101)
            .setSeance_id(1000));
        seances.add(new SeanceResponse()
            .setMovie_id(101)
            .setSeance_id(1020));

        List<ReservationResponse> reservations = new ArrayList<>();
        reservations.add(new ReservationResponse()
                .setId(1)
                .setSeance_id(1000)
                .setBill_id(500));
        reservations.add(new ReservationResponse()
                .setId(2)
                .setSeance_id(1000)
                .setBill_id(560));

        given(reservationService.findSeancesByMovie(101)).willReturn(seances);
        given(reservationService.findReservationBySeance(1000)).willReturn(reservations);
        given(reservationService.findReservationBySeance(1020)).willReturn(null);

        // Act
        mvc.perform(delete("/movie/101"))
                .andExpect(status().isNoContent());

        // Assert
        verify(reservationService, times(1)).deleteSeance(1000);
        verify(reservationService, times(1)).deleteSeance(1020);

        verify(paymentService, times(1)).deleteBill(500);
        verify(paymentService, times(1)).deleteBill(560);

        verify(movieService, times(1)).deleteMovie(101);
    }

    @Test
    public void rateMovie() throws Exception {
        // Arrange
        RatingRequest request = new RatingRequest()
                .setMovieId(100)
                .setScore(30)
                .setUserId(50);

        // Act
        mvc.perform(post("/rate").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Assert
        verify(movieService, times(1)).rateMovie(50,100,30);
    }

    @Test
    public void getSeances() throws Exception {
        // Arrange
        List<SeanceResponse> seances = new ArrayList<>();
        seances.add(new SeanceResponse()
            .setSeance_id(100)
            .setMovie_id(1));
        seances.add(new SeanceResponse()
            .setSeance_id(123)
            .setMovie_id(1));

        MovieResponse movieResponse = new MovieResponse()
                .setMovieId(1)
                .setName("Movie 2: Electric Boogaloo")
                .setDescription("Description")
                .setAverageRating(70);

        given(reservationService.findSeancesByMovie(1)).willReturn(seances);
        given(movieService.findMovie(1)).willReturn(movieResponse);

        // Act
        MvcResult result = mvc.perform(get("/movie/1/seance").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        SeanceComposite[] composites = mapper.readValue(result.getResponse().getContentAsString(),
                SeanceComposite[].class);

        // Assert
        List<SeanceComposite> r = seances.stream().map(x -> SeanceComposite.from(x, movieResponse)).collect(Collectors.toList());
        assertThat(r).containsExactlyInAnyOrder(composites);
    }

    @Test
    public void getSeanceById() throws Exception {
        // Arrange
        SeanceResponse seance = new SeanceResponse()
                .setSeance_id(117)
                .setMovie_id(152);

        MovieResponse movie = new MovieResponse()
                .setMovieId(152)
                .setName("Test Movie")
                .setDescription("Description");

        given(reservationService.findSeance(117)).willReturn(seance);
        given(movieService.findMovie(152)).willReturn(movie);

        // Act
        MvcResult result = mvc.perform(get("/seance/117").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        SeanceComposite composite = mapper.readValue(result.getResponse().getContentAsString(),
                SeanceComposite.class);

        // Assert
        assertThat(composite).isEqualTo(SeanceComposite.from(seance, movie));
    }

    @Test
    public void createSeance() throws Exception {
        // Arrange
        List<SeatResponse> seats = new ArrayList<>();
        seats.add(new SeatResponse()
            .setId(1)
            .setIsAvailable(true)
            .setSeance_id(100)
            .setSeatType(SeatType.Normal));
        seats.add(new SeatResponse()
                .setId(2)
                .setIsAvailable(false)
                .setSeance_id(100)
                .setSeatType(SeatType.Elite));

        SeanceResponse seance = new SeanceResponse()
                .setSeance_id(100)
                .setMovie_id(57)
                .setSeats(seats);

        SeanceRequest request = new SeanceRequest()
                .setMovieId(57);

        MovieResponse movie = new MovieResponse()
                .setMovieId(57)
                .setName("Movie!")
                .setDescription("description");

        given(reservationService.createSeance(57, null)).willReturn(seance);
        given(movieService.findMovie(57)).willReturn(movie);

        // Act
        MvcResult result = mvc.perform(post("/seance").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        SeanceComposite composite = mapper.readValue(result.getResponse().getContentAsString(),
                SeanceComposite.class);

        // Assert
        assertThat(composite).isEqualTo(SeanceComposite.from(seance, movie));
    }

    @Test
    public void deleteSeance() throws Exception {
        // Arrange
        List<ReservationResponse> reservations = new ArrayList<>();
        reservations.add(new ReservationResponse()
            .setId(1)
            .setSeance_id(100)
            .setBill_id(2000));
        reservations.add(new ReservationResponse()
            .setId(1)
            .setSeance_id(100)
            .setBill_id(3000));

        given(reservationService.findReservationBySeance(100)).willReturn(reservations);

        // Act
        mvc.perform(delete("/seance/100"))
                .andExpect(status().isNoContent());

        // Assert
        verify(paymentService, times(1)).deleteBill(2000);
        verify(paymentService, times(1)).deleteBill(3000);
        verify(reservationService, times(1)).deleteSeance(100);
    }

    @Test
    public void getReservation() throws Exception {
        // Arrange
        ReservationResponse reservation = new ReservationResponse()
                .setId(100)
                .setBill_id(5)
                .setSeance_id(2000)
                .setUser_id(1);

        BillResponse bill = new BillResponse()
                .setBillId(5)
                .setAmount(50000);

        given(reservationService.findReservation(100)).willReturn(reservation);
        given(paymentService.findBill(5)).willReturn(bill);

        // Act
        MvcResult result = mvc.perform(get("/reservation/100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ReservationComposite composite = mapper.readValue(result.getResponse().getContentAsString(),
                ReservationComposite.class);

        // Assert
        assertThat(composite).isEqualTo(ReservationComposite.from(reservation, bill));
    }

    @Test
    public void getReservationsForUser() throws Exception {
        // Arrange
        List<ReservationResponse> reservations = new ArrayList<>();
        reservations.add(new ReservationResponse()
                .setId(1)
                .setSeance_id(110)
                .setBill_id(2000)
                .setUser_id(5));
        reservations.add(new ReservationResponse()
                .setId(1)
                .setSeance_id(100)
                .setBill_id(3000)
                .setUser_id(5));

        BillResponse bill1 = new BillResponse()
                .setBillId(2000)
                .setAmount(5000);

        BillResponse bill2 = new BillResponse()
                .setBillId(3000)
                .setAmount(10000);

        given(reservationService.getReservationByUser(5)).willReturn(reservations);
        given(paymentService.findBill(2000)).willReturn(bill1);
        given(paymentService.findBill(3000)).willReturn(bill2);

        // Act
        MvcResult result = mvc.perform(get("/user/5/reservation").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ReservationComposite[] composites = mapper.readValue(result.getResponse().getContentAsString(),
                ReservationComposite[].class);

        // Assert
        List<ReservationComposite> r = new ArrayList<>();
        r.add(ReservationComposite.from(reservations.get(0), bill1));
        r.add(ReservationComposite.from(reservations.get(1), bill2));

        assertThat(r).containsExactlyInAnyOrder(composites);
    }

    @Test
    public void createReservation() throws Exception {
        // Arrange
        List<SeatResponse> seats = new ArrayList<>();
        seats.add(new SeatResponse(1, 100, SeatType.Normal, true));
        seats.add(new SeatResponse(2, 100, SeatType.Elite, true));

        ReservationResponse reservation = new ReservationResponse()
                .setId(1)
                .setSeance_id(100)
                .setUser_id(200)
                .setBill_id(500)
                .setSeats(seats);
        ReservationRequest request = new ReservationRequest()
                .setSeanceId(100)
                .setUserId(200)
                .setSeatIds(Arrays.asList(1,2,3));

        BillResponse bill = new BillResponse()
                .setBillId(500)
                .setAmount(6000);

        given(reservationService.createReservation(100, 200, Arrays.asList(1,2,3)))
                .willReturn(reservation);
        given(paymentService.findBill(500)).willReturn(bill);

        // Act
        MvcResult result = mvc.perform(post("/reservation").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        ReservationComposite composite = mapper.readValue(result.getResponse().getContentAsString(),
                ReservationComposite.class);

        // Assert
        assertThat(composite).isEqualTo(ReservationComposite.from(reservation, bill));
    }

    @Test
    public void deleteReservation() throws Exception {
        // Arrange
        ReservationResponse reservation = new ReservationResponse()
                .setId(54)
                .setSeance_id(100)
                .setUser_id(200)
                .setBill_id(500);

        given(reservationService.findReservation(54)).willReturn(reservation);

        // Act
        MvcResult result = mvc.perform(delete("/reservation/54"))
                .andExpect(status().isNoContent())
                .andReturn();

        // Assert
        verify(paymentService, times(1)).deleteBill(500);
        verify(reservationService, times(1)).deleteReservation(54);
    }

}