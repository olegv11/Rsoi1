package ru.oleg.rsoi.domain.movies.rating;

import lombok.Data;
import ru.oleg.rsoi.domain.movies.movie.Movie;

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
    private Integer user_id;

    @Column
    private Integer score;

}