package ru.oleg.rsoi.errors;

public class ApiErrorViewException extends RuntimeException {

    private ApiErrorView view;

    public ApiErrorViewException(ApiErrorView view) {
        this.view = view;
    }

    public ApiErrorView getView() {
        return view;
    }
}
