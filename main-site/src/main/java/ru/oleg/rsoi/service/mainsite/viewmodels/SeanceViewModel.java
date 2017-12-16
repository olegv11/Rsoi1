package ru.oleg.rsoi.service.mainsite.viewmodels;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SeanceViewModel {
    @NotNull
    @Pattern(regexp = "([0-9]{2})/([0-9]{2})/([0-9]{4})", message = "Date must be dd/mm/yyyy")
    private String date;
    private Integer movieId;
}
