package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class CannotAddContributorException extends ShootrException {

    public CannotAddContributorException(Throwable cause) {
        super(cause);
    }
}
