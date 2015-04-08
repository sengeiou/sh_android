package com.shootr.android.data.entity;

import java.io.Serializable;

public class UserEntity extends Synchronized implements Serializable, Comparable<UserEntity>, Cloneable{

    private Long idUser;
    private Long favoriteTeamId;
    private String favoriteTeamName;
    private String sessionToken;
    private String userName;
    private String email;
    private String name;
    private String photo;
    private Long points;
    private Long numFollowings;
    private Long numFollowers;
    private Long rank;
    private String website;
    private String bio;
    private String eventTitle;
    private Long idEvent;
    private String status;
    private Integer checkIn;

    public UserEntity(){
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getFavoriteTeamId() {
        return favoriteTeamId;
    }

    public void setFavoriteTeamId(Long favoriteTeamId) {
        this.favoriteTeamId = favoriteTeamId;
    }

    public String getFavoriteTeamName() {
        return favoriteTeamName;
    }

    public void setFavoriteTeamName(String favoriteTeamName) {
        this.favoriteTeamName = favoriteTeamName;
    }

    public String getSessionToken() {
        //Not implemented in domain
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
            // Timber.w(e, "UserModel has thrown CloneNotSupportedException. This should never happen. Returning the current instance. Be carreful, young padawan.");
            return this;
        }
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        if (idEvent!=null && idEvent > 0) {
            this.idEvent = idEvent;
        } else {
            this.idEvent = null;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Integer checkIn) {
        this.checkIn = checkIn;
    }
}
