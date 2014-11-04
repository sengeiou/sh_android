package com.shootr.android.ui.model;

import java.io.Serializable;

public class UserModel implements Serializable {

    private Long idUser;
    private String favoriteTeamName;
    private Long favoriteTeamId;
    private String userName;
    private String name;
    private String photo;
    private Long numFollowings;
    private Long numFollowers;
    private String website;
    private String bio;
    private int relationship;
    private Long points;

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getFavoriteTeamId() {
        return favoriteTeamId;
    }

    public void setFavoriteTeamId(Long favoriteTeamId) {
        this.favoriteTeamId = favoriteTeamId;
    }
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }
}
