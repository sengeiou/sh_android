package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.ShootrException;

public class CannotFollowBlockedUserException extends ShootrException {

    public CannotFollowBlockedUserException(String message) {
        super(message);
    }

    public CannotFollowBlockedUserException(Throwable cause) {
        super(cause);
    }
}
