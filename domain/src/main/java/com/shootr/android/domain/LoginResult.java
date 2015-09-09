package com.shootr.android.domain;

public class LoginResult {

    private User user;
    private String sessionToken;
    private Boolean isNewUser;

    public LoginResult(User user, String sessionToken) {
        this.user = user;
        this.sessionToken = sessionToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginResult)) return false;

        LoginResult that = (LoginResult) o;

        if (!user.equals(that.user)) return false;
        return sessionToken.equals(that.sessionToken);
    }

    @Override public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + sessionToken.hashCode();
        return result;
    }

    @Override public String toString() {
        return "LoginResult{" +
          "user=" + user +
          ", sessionToken='" + sessionToken + '\'' +
          '}';
    }

    public Boolean isNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(Boolean isNewUser) {
        this.isNewUser = isNewUser;
    }
}
