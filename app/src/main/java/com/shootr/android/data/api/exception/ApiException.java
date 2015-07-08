package com.shootr.android.data.api.exception;

public class ApiException extends Exception {

    private ErrorInfo errorInfo;

    public ApiException(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
