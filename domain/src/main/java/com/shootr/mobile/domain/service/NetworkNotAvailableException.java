package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class NetworkNotAvailableException extends ShootrException {

    public NetworkNotAvailableException(String message) {
        super(message);
    }

    public NetworkNotAvailableException(Throwable cause) {
        super(cause);
    }
}
