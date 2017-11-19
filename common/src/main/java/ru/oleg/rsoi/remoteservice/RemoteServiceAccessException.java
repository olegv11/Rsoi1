package ru.oleg.rsoi.remoteservice;

public class RemoteServiceAccessException extends RemoteServiceException {

    private String url;

    public RemoteServiceAccessException(String message, String url) {
        super(message);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}