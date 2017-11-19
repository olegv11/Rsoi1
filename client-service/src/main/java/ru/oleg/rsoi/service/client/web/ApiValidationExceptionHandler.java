package ru.oleg.rsoi.service.client.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.oleg.rsoi.errors.ApiErrorView;
import ru.oleg.rsoi.errors.ApiFieldError;
import ru.oleg.rsoi.errors.ApiGlobalError;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiValidationExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        BindingResult bindingResult = ex.getBindingResult();

        List<ApiFieldError> apiFieldErrors = bindingResult
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiFieldError(
                        fieldError.getField(),
                        fieldError.getCode(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        List<ApiGlobalError> apiGlobalErrors = bindingResult
                .getGlobalErrors()
                .stream()
                .map(globalError -> new ApiGlobalError(
                        globalError.getCode()
                ))
                .collect(Collectors.toList());

        ApiErrorView view = new ApiErrorView(apiFieldErrors, apiGlobalErrors);

        return new ResponseEntity<>(view, HttpStatus.BAD_REQUEST);
    }
}
