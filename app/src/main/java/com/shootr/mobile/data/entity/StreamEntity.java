package com.shootr.mobile.data.entity;

import java.util.List;

public class StreamEntity extends Synchronized implements Comparable<StreamEntity> {

    private String idStream;
    private String idUser;
    private String userName;
    private String shortTitle;
    private String description;
    private String topic;
    private String title;
    private String photo;
    private Integer notifyCreation;
    private String country;
    private Integer mediaCountByRelatedUsers;
    private Integer removed;
    private List<UserEntity> watchers;
    private Long totalFavorites;
    private Long totalWatchers;
    private Long historicWatchers;
    private Long totalShots;
    private Long uniqueShots;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof StreamEntity)) {
            return false;
        }
        StreamEntity that = (StreamEntity) o;

        if (idStream != null ? !idStream.equals(that.idStream) : that.idStream != null) {
            return false;
        }
        return true;
    }

    @Override public int hashCode() {
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

    public Long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Long getTotalWatchers() {
        return totalWatchers;
    }

    public void setTotalWatchers(Long totalWatchers) {
        this.totalWatchers = totalWatchers;
    }

    public Long getHistoricWatchers() {
        return historicWatchers;
    }

    public void setHistoricWatchers(Long historicWatchers) {
        this.historicWatchers = historicWatchers;
    }

    public Long getTotalShots() {
        return totalShots;
    }

    public void setTotalShots(Long totalShots) {
        this.totalShots = totalShots;
    }

    public Long getUniqueShots() {
        return uniqueShots;
    }

    public void setUniqueShots(Long uniqueShots) {
        this.uniqueShots = uniqueShots;
    }
}
