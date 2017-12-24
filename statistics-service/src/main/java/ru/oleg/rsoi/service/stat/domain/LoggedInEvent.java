package ru.oleg.rsoi.service.stat.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "loggedinevent")
public class LoggedInEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
}
