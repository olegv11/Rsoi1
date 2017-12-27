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
@Table(name = "visitedMovie")
public class VisitedMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer movieId;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
}
