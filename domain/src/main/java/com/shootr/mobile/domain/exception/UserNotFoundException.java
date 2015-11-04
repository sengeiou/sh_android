package com.shootr.mobile.domain.exception;

public class UserNotFoundException extends ShootrException{

    private String username;

    public UserNotFoundException(String username) {
        super(String.format("User not found with username='%s'", username));
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserNotFoundException)) return false;

        UserNotFoundException that = (UserNotFoundException) o;

        return !(username != null ? !username.equals(that.username) : that.username != null);
    }

    @Override public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override public String toString() {
        return "UserNotFoundException{" +
          "username='" + username + '\'' +
          '}';
    }
}
