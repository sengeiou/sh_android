package com.shootr.mobile.domain.validation;

public class FieldValidationError extends ValidationError {

    private final int field;

    public FieldValidationError(String errorCode, int field) {
        super(errorCode);
        this.field = field;
    }

    public int getField() {
        return field;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldValidationError)) return false;
        if (!super.equals(o)) return false;

        FieldValidationError that = (FieldValidationError) o;

        return field == that.field;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + field;
        return result;
    }

    @Override public String toString() {
        return "FieldValidationError{" +
          "field=" + field +
          "} " + super.toString();
    }
}
