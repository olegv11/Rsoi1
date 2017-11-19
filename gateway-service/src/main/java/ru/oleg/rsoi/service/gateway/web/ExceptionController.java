package ru.oleg.rsoi.service.reservation.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.oleg.rsoi.dto.ErrorResponse;
import ru.oleg.rsoi.errors.ApiErrorView;
import ru.oleg.rsoi.errors.ApiErrorViewException;
import ru.oleg.rsoi.remoteservice.RemoteServiceException;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse notFound(EntityNotFoundException exception) {
        logger.error("Entity Not Found exception:" + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse exception(Exception exception) {
        logger.error("Internal server error:" + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RemoteServiceException.class)
    public ErrorResponse remoteServiceError(RemoteServiceException exception) {
        logger.error("Remote service error:" + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApiErrorViewException.class)
    public ApiErrorView apiError(ApiErrorViewException exception) {
        return exception.getView();
    }

}

