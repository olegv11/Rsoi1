package ru.oleg.rsoi.service.movie.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rating")
@ToString(exclude = "movie")
@EqualsAndHashCode(exclude = "movie")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column
    private Integer userId;

    @Column
    private Integer score;

}