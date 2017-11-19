package ru.oleg.rsoi.dto;

import javax.validation.ConstraintViolation;
import java.util.Collection;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super();
    }
    public ValidationException(String message) {
        super(message);
    }

    public static <T> ValidationException CreateValidationException(Collection<ConstraintViolation<T>> violations) {
        if (violations == null || violations.isEmpty()) {
            return new ValidationException();
        }

        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<T> violation : violations) {
            sb.append(violation.getMessage());
            sb.append("\n");
        }
        return new ValidationException(sb.toString());
    }
}
