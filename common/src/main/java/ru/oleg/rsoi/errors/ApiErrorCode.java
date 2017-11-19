package ru.oleg.rsoi.errors;

public enum ApiErrorCode {
    GENERIC("Generic error"),
    SEAT_NOT_EXISTS("Seat does not exist"),
    SEAT_TAKEN("Seat already taken");

    private String code;

    ApiErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
