package com.shootr.android.ui.model;

import java.io.Serializable;
import timber.log.Timber;

public class UserModel implements Serializable, Cloneable {

    private String idUser;
    private String userName;
    private String name;
    private String photo;
    private Long numFollowings;
    private Long numFollowers;
    private String website;
    private String bio;
    private int relationship;
    private Long points;
    private String eventWatchingId;
    private String eventWatchingTitle;
    private Long joinEventDate;
    private String joinEventDateText;
    private String idCheckedEvent;

    public String getIdCheckedEvent() {
        return idCheckedEvent;
    }

    public void setIdCheckedEvent(String idCheckedEvent) {
        this.idCheckedEvent = idCheckedEvent;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
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

    @Override public UserModel clone() {
        try {
            return (UserModel) super.clone();
        } catch (CloneNotSupportedException e) {
            Timber.w(e, "UserModel has thrown CloneNotSupportedException. This should never happen. Returning the current instance. Be carreful, young padawan.");
            return this;
        }
    }

    @Override public String toString() {
        return "UserModel{" +
          "idUser=" + idUser +
          ", userName='" + userName + '\'' +
          '}';
    }

    public String getEventWatchingId() {
        return eventWatchingId;
    }

    public void setEventWatchingId(String eventWatchingId) {
        this.eventWatchingId = eventWatchingId;
    }

    public String getEventWatchingTitle() {
        return eventWatchingTitle;
    }

    public void setEventWatchingTitle(String eventWatchingTitle) {
        this.eventWatchingTitle = eventWatchingTitle;
    }

    public Long getJoinEventDate() {
        return joinEventDate;
    }

    public void setJoinEventDate(Long joinEventDate) {
        this.joinEventDate = joinEventDate;
    }

    public String getJoinEventDateText() {
        return joinEventDateText;
    }

    public void setJoinEventDateText(String joinEventDateText) {
        this.joinEventDateText = joinEventDateText;
    }
}
