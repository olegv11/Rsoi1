package ru.oleg.rsoi.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiGlobalError {
    private String code;
}
