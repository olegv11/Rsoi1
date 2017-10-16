package ru.oleg.rsoi.service;

import ru.oleg.rsoi.domain.reservations.Seance;

public interface SeanceService {
    Seance getById(Integer id);
    Seance save(Seance seance);

}
