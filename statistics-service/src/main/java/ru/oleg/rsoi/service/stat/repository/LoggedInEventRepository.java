package ru.oleg.rsoi.service.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oleg.rsoi.service.stat.domain.LoggedInEvent;

public interface LoggedInEventRepository extends JpaRepository<LoggedInEvent, Integer> {
}
