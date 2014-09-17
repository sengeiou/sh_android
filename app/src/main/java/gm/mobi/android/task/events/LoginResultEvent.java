package gm.mobi.android.task.events;


import gm.mobi.android.db.objects.User;

public class LoginResultEvent {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_INVALID = 2;
    public static final int STATUS_SERVER_FAILURE = 3;

    private Exception error;
    private int status;
    private User signedUser;
    private String code, message;
    // Private constructor, create through static methods
    private LoginResultEvent(int status) {
        this.status = status;
    }

    private LoginResultEvent(int status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static LoginResultEvent successful(User user) {
        LoginResultEvent loginResultEvent = new LoginResultEvent(STATUS_SUCCESS);
        loginResultEvent.setSignedUser(user);
        return loginResultEvent;
    }

    public static LoginResultEvent invalid() {
        return new LoginResultEvent(STATUS_INVALID);
    }

    public static LoginResultEvent serverError(String code, String message){
        return new LoginResultEvent(STATUS_SERVER_FAILURE,code,message);
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

    public User getSignedUser() {
        return signedUser;
    }

    public void setSignedUser(User signedUser) {
        this.signedUser = signedUser;
    }
}
