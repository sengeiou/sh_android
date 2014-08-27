package gm.mobi.android.task.events;

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
