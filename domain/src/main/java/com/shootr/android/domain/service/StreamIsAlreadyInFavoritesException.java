package com.shootr.android.domain.service;

import com.shootr.android.domain.exception.ShootrException;

public class StreamIsAlreadyInFavoritesException extends ShootrException {

    public StreamIsAlreadyInFavoritesException(String message) {
        super(message);
    }

    public StreamIsAlreadyInFavoritesException(Throwable cause) {
        super(cause);
    }

}
