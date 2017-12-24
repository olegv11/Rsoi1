package ru.oleg.rsoi.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsItem implements Serializable {
    private int Id;
    public String data;
}
