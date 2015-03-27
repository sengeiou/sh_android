package com.shootr.android.domain;

import java.util.Comparator;

public class User {

    private Long idUser;
    private String favoriteTeamName;
    private Long favoriteTeamId;
    private String username;
    private String name;
    private String email;
    private String photo;
    private Long numFollowings;
    private Long numFollowers;
    private String website;
    private String bio;
    private Long points;

    private Long visibleEventId;
    private String visibleEventTitle;
    private String status;

    private boolean isFollowing;
    private boolean isFollower;
    private boolean isMe;

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getFavoriteTeamName() {
        return favoriteTeamName;
    }

    public void setFavoriteTeamName(String favoriteTeamName) {
        this.favoriteTeamName = favoriteTeamName;
    }

    public Long getFavoriteTeamId() {
        return favoriteTeamId;
    }

    public void setFavoriteTeamId(Long favoriteTeamId) {
        this.favoriteTeamId = favoriteTeamId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getNumFollowings() {
        return numFollowings;
    }

    public void setNumFollowings(Long numFollowings) {
        this.numFollowings = numFollowings;
    }

    public Long getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Long numFollowers) {
        this.numFollowers = numFollowers;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean isFollower) {
        this.isFollower = isFollower;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!idUser.equals(user.idUser)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idUser.hashCode();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getVisibleEventId() {
        return visibleEventId;
    }

    public void setVisibleEventId(Long visibleEventId) {
        this.visibleEventId = visibleEventId;
    }

    public String getVisibleEventTitle() {
        return visibleEventTitle;
    }

    public void setVisibleEventTitle(String visibleEventTitle) {
        this.visibleEventTitle = visibleEventTitle;
    }

    public String getStatus() {
        return status;
    }

    @Override public String toString() {
        return "User{" +
          "idUser=" + idUser +
          ", username='" + username + '\'' +
          ", visibleEventId=" + visibleEventId +
          ", status='" + status + '\'' +
          '}';
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class UsernameComparator implements Comparator<User> {

        @Override public int compare(User o1, User o2) {
            return o1.getUsername().compareTo(o2.getUsername());
        }
    }
}
