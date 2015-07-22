package com.shootr.android.task.events.loginregister;

public class RegistrationCompletedStream {

    private Exception error;

    public RegistrationCompletedStream() {

    }

    public RegistrationCompletedStream(Exception error) {
        this.error = error;
    }


    public Exception getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

}
