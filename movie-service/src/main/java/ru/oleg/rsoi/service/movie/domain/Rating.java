package ru.oleg.rsoi.service.movie.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rating")
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