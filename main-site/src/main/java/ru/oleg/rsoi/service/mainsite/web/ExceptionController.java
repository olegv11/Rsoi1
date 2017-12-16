package ru.oleg.rsoi.service.mainsite.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.oleg.rsoi.dto.ErrorResponse;
import ru.oleg.rsoi.errors.ApiErrorView;
import ru.oleg.rsoi.errors.ApiErrorViewException;
import ru.oleg.rsoi.remoteservice.RemoteServiceAccessException;
import ru.oleg.rsoi.remoteservice.RemoteServiceException;

@ControllerAdvice
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse exception(Exception exception) {
        logger.error("Internal server error:" + exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RemoteServiceException.class)
    public String remoteServiceError(RemoteServiceException exception, Model model) {
        logger.error("Remote service error:" + exception.getMessage());
        model.addAttribute("message", exception.getMessage());
        return "errormessage";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RemoteServiceAccessException.class)
    public String remoteserviceaccessError(RemoteServiceAccessException ex) {
        return "ConnectionError";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApiErrorViewException.class)
    public ApiErrorView apiError(ApiErrorViewException exception) {
        return exception.getView();
    }
}
