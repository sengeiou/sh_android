package gm.mobi.android.task.events;


public class LoginResultEvent {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_INVALID = 2;

    private Exception error;
    private int status;

    // Private constructor, create through static methods
    private LoginResultEvent(int status) {
        this.status = status;
    }

    //TODO pasar los parámetros necesarios para mantener la sesión
    public static LoginResultEvent successful() {
        return new LoginResultEvent(STATUS_SUCCESS);
    }

    public static LoginResultEvent invalid() {
        return new LoginResultEvent(STATUS_INVALID);
    }

    public int getStatus() {
        return status;
    }

    public Exception getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}
