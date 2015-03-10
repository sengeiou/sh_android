package com.shootr.android.domain.exception;

public class RepositoryException extends ShootrException {

    public RepositoryException(Throwable cause) {
        super(cause);
    }

    public RepositoryException(String cause) {
        super(cause);
    }

    public RepositoryException() {
        super();
    }
}
