package ru.oleg.rsoi.remoteservice;

import org.springframework.http.HttpStatus;

public class RemoteServiceException extends RuntimeException {

    private HttpStatus status;

    public RemoteServiceException() {
        super();
    }

    public RemoteServiceException(String message) {
        super(message);
    }

    public RemoteServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
