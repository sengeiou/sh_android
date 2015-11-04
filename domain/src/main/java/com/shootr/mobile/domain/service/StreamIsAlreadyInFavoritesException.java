package com.shootr.mobile.domain.service;

public class StreamIsAlreadyInFavoritesException extends com.shootr.mobile.domain.exception.ShootrException {

    public StreamIsAlreadyInFavoritesException(String message) {
        super(message);
    }

    public StreamIsAlreadyInFavoritesException(Throwable cause) {
        super(cause);
    }

}
