package ru.oleg.rsoi.service.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.oleg.rsoi.dto.reservation.SeanceRequest;
import ru.oleg.rsoi.dto.reservation.SeatType;
import ru.oleg.rsoi.remoteservice.RemoteMovieService;
import ru.oleg.rsoi.service.reservation.domain.Seance;
import ru.oleg.rsoi.service.reservation.domain.Seat;
import ru.oleg.rsoi.service.reservation.repository.SeanceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatPriceRepository;
import ru.oleg.rsoi.service.reservation.repository.SeatRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeanceServiceImpl implements SeanceService {

    @Autowired
    RemoteMovieService remoteMovieService;

    @Autowired
    SeanceRepository seanceRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    SeatPriceRepository seatPriceRepository;

    @Override
    public Seance getById(Integer id) {
        Seance seance = seanceRepository.findOne(id);
        if (seance == null) {
            throw new EntityNotFoundException("Seance("+id+") not found.");
        }
        return seance;
    }

    @Override
    public List<Seance> getByMovie(Integer movie_id) {
        return seanceRepository.findAllByMovieId(movie_id);
    }

    @Override
    public Seance createSeance(SeanceRequest seanceRequest) {
        if (!remoteMovieService.movieExists(seanceRequest.getMovieId())) {
            throw new EntityNotFoundException("Movie("+seanceRequest.getMovieId()+") does not exist.");
        }

        Seance seance = new Seance()
                .setMovieId(seanceRequest.getMovieId())
                .setScreenDate(seanceRequest.getScreenDate());

        createSeatsForSeance(seance);

        return seanceRepository.save(seance);
    }

    @Override
    public void deleteSeance(Integer id) {
        seanceRepository.delete(id);
    }

    private void createSeatsForSeance(Seance seance) {
        ArrayList<Seat> seats = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Seat seat = new Seat()
                    .setSeance(seance)
                    .setSeatType(SeatType.Normal)
                    .setAvailable(true);
            seats.add(seat);
        }

        for (int i = 0; i < 5; i++) {
            Seat seat = new Seat()
                    .setSeance(seance)
                    .setSeatType(SeatType.Elite)
                    .setAvailable(true);
            seats.add(seat);
        }

        seance.setSeats(seats);
    }
}
