package com.shootr.android.domain.service.shot;

import com.shootr.android.domain.exception.ShootrException;

public class DeletedShotException extends ShootrException {

    public DeletedShotException(String message) {
        super(message);
    }

    public DeletedShotException(Throwable cause) {
        super(cause);
    }

}
