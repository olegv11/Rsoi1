package ru.oleg.rsoi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by romanow on 31.10.16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
}