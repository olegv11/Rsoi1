package ru.oleg.rsoi.service.reservation.service;

import ru.oleg.rsoi.dto.SeanceRequest;
import ru.oleg.rsoi.service.reservation.domain.Seance;

import java.util.List;

public interface SeanceService {
    Seance getById(Integer id);
    List<Seance> getByMovie(Integer movie_id);
    Seance createSeance(SeanceRequest seanceRequest);
    void deleteSeance(Integer id);
}
