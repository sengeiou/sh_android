package com.shootr.android.domain;

public class Watch {

    private User user;
    private Long idEvent;
    private String userStatus;
    private boolean watching;
    private boolean notificaticationsEnabled;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isWatching() {
        return watching;
    }

    public void setWatching(boolean watching) {
        this.watching = watching;
    }

    public boolean isNotificaticationsEnabled() {
        return notificaticationsEnabled;
    }

    public void setNotificaticationsEnabled(boolean notificaticationsEnabled) {
        this.notificaticationsEnabled = notificaticationsEnabled;
    }
}
