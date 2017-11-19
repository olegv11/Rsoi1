package ru.oleg.rsoi.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiErrorView {
    private List<ApiFieldError> fieldErrors;
    private List<ApiGlobalError> globalErrors;
}
