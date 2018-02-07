package com.company.rest.api.common;

public class RestApiError {
    private int errorCode;
    private String errorMessage;

    public RestApiError(int errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
