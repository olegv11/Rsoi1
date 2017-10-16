package ru.oleg.rsoi.domain.movies.movie;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.domain.movies.rating.Rating;

import javax.persistence.*;
import java.util.Set;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rating> ratings;
}

