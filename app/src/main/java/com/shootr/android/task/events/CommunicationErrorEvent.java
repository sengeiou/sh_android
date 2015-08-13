package com.shootr.android.task.events;

public class CommunicationErrorEvent {

    private Exception exception;

    public CommunicationErrorEvent(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
