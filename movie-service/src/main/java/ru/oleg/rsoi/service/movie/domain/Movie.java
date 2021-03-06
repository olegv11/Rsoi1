package ru.oleg.rsoi.service.movie.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.oleg.rsoi.dto.movie.MovieResponse;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
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

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Rating> ratings = new HashSet<Rating>();

    public MovieResponse toResponse() {
        return new MovieResponse(id, name, description,
                ratings == null ? 0 : ratings.stream().mapToInt(Rating::getScore).average().orElse(0));
    }
}

