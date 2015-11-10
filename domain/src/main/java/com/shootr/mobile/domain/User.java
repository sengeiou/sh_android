package com.shootr.mobile.domain;

import java.util.Comparator;

public class User {

    private String idUser;
    private String username;
    private String name;
    private String email;
    private Boolean emailConfirmed;
    private Boolean verifiedUser;
    private String photo;
    private Long numFollowings;
    private Long numFollowers;
    private String website;
    private String bio;
    private Long points;
    private Long joinStreamDate;

    private String idWatchingStream;
    private String watchingStreamTitle;

    private boolean isFollowing;
    private boolean isFollower;
    private boolean isMe;

    private EntityMetadata metadata;

    private Long createdStreamsCount;
    private Long favoritedStreamsCount;

    public Long getFavoritedStreamsCount() {
        return favoritedStreamsCount;
    }

    public void setFavoritedStreamsCount(Long favoritedStreamsCount) {
        this.favoritedStreamsCount = favoritedStreamsCount;
    }

    public Long getCreatedStreamsCount() {
        return createdStreamsCount;
    }

    public void setCreatedStreamsCount(Long createdStreamsCount) {
        this.createdStreamsCount = createdStreamsCount;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!idUser.equals(user.idUser)) return false;

        return true;
    }

    @Override public int hashCode() {
        return idUser.hashCode();
    }

    public String getIdWatchingStream() {
        return idWatchingStream;
    }

    public void setIdWatchingStream(String idWatchingStream) {
        this.idWatchingStream = idWatchingStream;
    }

    public String getWatchingStreamTitle() {
        return watchingStreamTitle;
    }

    public void setWatchingStreamTitle(String watchingStreamTitle) {
        this.watchingStreamTitle = watchingStreamTitle;
    }

    @Override public String toString() {
        return "User{" +
          "idUser=" + idUser +
          ", username='" + username + '\'' +
          ", idWatchingStream=" + idWatchingStream +
          '}';
    }

    public Long getJoinStreamDate() {
        return joinStreamDate;
    }

    public void setJoinStreamDate(Long joinStreamDate) {
        this.joinStreamDate = joinStreamDate;
    }

    public EntityMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(EntityMetadata metadata) {
        this.metadata = metadata;
    }

    public Boolean isVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(Boolean verifiedUser) {
        this.verifiedUser = verifiedUser;
    }

    public static class UsernameComparator implements Comparator<User> {

        @Override public int compare(User o1, User o2) {
            return o1.getUsername().compareTo(o2.getUsername());
        }
    }
}
