package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class StreamIsAlreadyInFavoritesException extends ShootrException {

    public StreamIsAlreadyInFavoritesException(String message) {
        super(message);
    }

    public StreamIsAlreadyInFavoritesException(Throwable cause) {
        super(cause);
    }
}
