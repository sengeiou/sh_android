package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.ShootrException;

public class InvalidCheckoutException extends ShootrException {

    public InvalidCheckoutException(String message) {
        super(message);
    }

    public InvalidCheckoutException(Throwable cause) {
        super(cause);
    }
}
