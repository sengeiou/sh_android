package com.shootr.android.data.entity;

import java.util.List;

public class StreamEntity extends Synchronized implements Comparable<StreamEntity> {

    private String idStream;
    private String idUser;
    private String userName;
    private String shortTitle;
    private String description;
    private String title;
    private String photo;
    private Integer notifyCreation;
    private String locale;
    private Integer mediaCountByRelatedUsers;
    private Integer removed;
    private List<UserEntity> watchers;

    public List<UserEntity> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<UserEntity> watchers) {
        this.watchers = watchers;
    }

    public Integer getRemoved() {
        return removed;
    }

    public void setRemoved(Integer removed) {
        this.removed = removed;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || !(o instanceof StreamEntity)){
            return false;
        }
        StreamEntity that = (StreamEntity) o;

        if (idStream != null ? !idStream.equals(that.idStream) : that.idStream != null){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return idStream != null ? idStream.hashCode() : 0;
    }

    @Override public int compareTo(StreamEntity another) {
        boolean areSameStream = this.getIdStream().equals(another.getIdStream());
        if (areSameStream) {
            return 0;
        }
        int idComparison = this.getIdStream().compareTo(another.getIdStream());
        return idComparison;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getNotifyCreation() {
        return notifyCreation;
    }

    public void setNotifyCreation(Integer notifyCreation) {
        this.notifyCreation = notifyCreation;
    }

    public Integer getMediaCountByRelatedUsers() {
        return mediaCountByRelatedUsers;
    }

    public void setMediaCountByRelatedUsers(Integer mediaCountByRelatedUsers) {
        this.mediaCountByRelatedUsers = mediaCountByRelatedUsers;
    }
}
