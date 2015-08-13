package com.shootr.android.task.events.loginregister;

public class RegistrationCompletedEvent {

    private Exception error;

    public RegistrationCompletedEvent() {

    }

    public RegistrationCompletedEvent(Exception error) {
        this.error = error;
    }


    public Exception getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

}
