package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class CannotFollowBlockedUserException extends ShootrException {

    public CannotFollowBlockedUserException(String message) {
        super(message);
    }

    public CannotFollowBlockedUserException(Throwable cause) {
        super(cause);
    }
}
