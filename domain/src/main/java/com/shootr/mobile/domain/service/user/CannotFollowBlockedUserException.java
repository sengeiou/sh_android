package com.shootr.mobile.domain.service.user;

public class CannotFollowBlockedUserException extends com.shootr.mobile.domain.exception.ShootrException {

    public CannotFollowBlockedUserException(String message) {
        super(message);
    }

    public CannotFollowBlockedUserException(Throwable cause) {
        super(cause);
    }
}
