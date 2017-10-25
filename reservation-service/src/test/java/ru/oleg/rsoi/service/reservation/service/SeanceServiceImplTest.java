package ru.oleg.rsoi.service.reservation.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.remoteservice.RemoteMovieService;
import ru.oleg.rsoi.service.reservation.domain.Seance;
import ru.oleg.rsoi.service.reservation.repository.SeanceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatPriceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatRepository;

import javax.persistence.EntityNotFoundException;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeanceServiceImplTest {

    @MockBean
    SeanceRepository seanceRepository;

    @MockBean
    SeatRepository seatRepository;

    @MockBean
    RemoteMovieService remoteMovieService;

    @MockBean
    SeatPriceRepository seatPriceRepository;

    @Autowired
    SeanceService seanceService;

    @Test(expected = EntityNotFoundException.class)
    public void getByIdThrowsOnNonexistentSeance() throws Exception {
        given(seanceRepository.findOne(10)).willReturn(null);

        seanceService.getById(10);
    }

    @Test
    public void getByIdReturnsSeance() throws Exception {
        // Arrange
        Seance seance = new Seance()
                .setId(10)
                .setMovieId(100);

        given(seanceRepository.findOne(10)).willReturn(seance);

        // Act
        Seance result = seanceService.getById(10);

        // Assert
        assertThat(result).isEqualTo(seance);
    }

    @Test(expected = EntityNotFoundException.class)
    public void createSeanceThrowsWhenMovieDoesNotExist() {
        given(remoteMovieService.movieExists(anyInt())).willReturn(false);

        SeanceRequest seanceRequest = new SeanceRequest(1, new Date());
        seanceService.createSeance(seanceRequest);
    }

    @Test
    public void createSeance() {
        // Arrange
        given(remoteMovieService.movieExists(100)).willReturn(true);

        SeanceRequest seanceRequest = new SeanceRequest(100, new Date());
        Seance seance = new Seance()
                .setId(1)
                .setMovieId(100)
                .setScreenDate(seanceRequest.getScreenDate());
        given(seanceRepository.findOne(1)).willReturn(seance);

        when(seanceRepository.save(any(Seance.class))).then(returnsFirstArg());
        // Act
        Seance result = seanceService.createSeance(seanceRequest);

        // Assert
        assertThat(seance.getMovieId()).isEqualTo(result.getMovieId());
        assertThat(seance.getScreenDate()).isEqualTo(result.getScreenDate());
    }


    @Test
    public void deleteSeance() throws Exception {
    }

}