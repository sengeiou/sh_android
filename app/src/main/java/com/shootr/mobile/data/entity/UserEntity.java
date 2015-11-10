package com.shootr.mobile.data.entity;

import java.io.Serializable;
import timber.log.Timber;

public class UserEntity extends Synchronized implements Serializable, Comparable<UserEntity>, Cloneable{

    private String idUser;
    private String sessionToken;
    private String userName;
    private String email;
    private Integer emailConfirmed;
    private Integer verifiedUser;
    private String name;
    private String photo;
    private Long points;
    private Long numFollowings;
    private Long numFollowers;
    private Long rank;
    private String website;
    private String bio;
    private String watchingStreamTitle;
    private String idWatchingStream;
    private Long joinStreamDate;
    private String watchSynchronizedStatus;
    private Long createdStreamsCount;
    private Long favoritedStreamsCount;

    public UserEntity(){
    }

    public Long getCreatedStreamsCount() {
        return createdStreamsCount;
    }

    public void setCreatedStreamsCount(Long createdStreamsCount) {
        this.createdStreamsCount = createdStreamsCount;
    }

    public Long getFavoritedStreamsCount() {
        return favoritedStreamsCount;
    }

    public void setFavoritedStreamsCount(Long favoritedStreamsCount) {
        this.favoritedStreamsCount = favoritedStreamsCount;
    }

    public Integer getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Integer emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
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

    public Long getRank() {
        //Not implemented in domain
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
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

    @Override public int compareTo(UserEntity another) {
        return this.getUserName().compareTo(another.getUserName());
    }

    @Override public boolean equals(Object o) {
        if (o instanceof UserEntity) {
            UserEntity otherUser = (UserEntity) o;
            return this.idUser.equals(otherUser.idUser);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return idUser.hashCode();
    }

    @Override
    public UserEntity clone(){
        try {
            return (UserEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            //TODO custom logger
            Timber.w(e,
              "UserModel has thrown CloneNotSupportedException. This should never happen. Returning the current instance. Be carreful, young padawan.");
            return this;
        }
    }

    public String getWatchingStreamTitle() {
        return watchingStreamTitle;
    }

    public void setWatchingStreamTitle(String watchingStreamTitle) {
        this.watchingStreamTitle = watchingStreamTitle;
    }

    public String getIdWatchingStream() {
        return idWatchingStream;
    }

    public void setIdWatchingStream(String idWatchingStream) {
        if (idWatchingStream !=null) {
            this.idWatchingStream = idWatchingStream;
        } else {
            this.idWatchingStream = null;
        }
    }

    public Long getJoinStreamDate() {
        return joinStreamDate;
    }

    public void setJoinStreamDate(Long joinStreamDate) {
        this.joinStreamDate = joinStreamDate;
    }

    public String getWatchSynchronizedStatus() {
        return watchSynchronizedStatus;
    }

    public void setWatchSynchronizedStatus(String watchSynchronizedStatus) {
        this.watchSynchronizedStatus = watchSynchronizedStatus;
    }

    public Integer getVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(Integer verifiedUser) {
        this.verifiedUser = verifiedUser;
    }
}
