package com.shootr.mobile.domain.validation;

public class ValidationError {

    private String errorCode;

    public ValidationError(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationError)) return false;

        ValidationError that = (ValidationError) o;

        return errorCode.equals(that.errorCode);
    }

    @Override public int hashCode() {
        return errorCode.hashCode();
    }

    @Override public String toString() {
        return "ValidationError{" +
          "errorCode='" + errorCode + '\'' +
          '}';
    }
}
