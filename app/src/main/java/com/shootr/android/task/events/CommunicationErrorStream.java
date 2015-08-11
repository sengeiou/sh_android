package com.shootr.android.task.events;

public class CommunicationErrorStream {

    private Exception exception;

    public CommunicationErrorStream(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
