package com.shootr.android.domain.exception;

public class ServerCommunicationException extends ShootrException {

    public ServerCommunicationException(Throwable cause) {
        super(cause);
    }

    public ServerCommunicationException(String message) {
        super(message);
    }
}
