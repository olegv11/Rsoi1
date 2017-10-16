package ru.oleg.rsoi.remoteservice;

public class RemoteServiceException extends RuntimeException {
    public RemoteServiceException() {
        super();
    }
    public RemoteServiceException(String message) {
        super(message);
    }
}
