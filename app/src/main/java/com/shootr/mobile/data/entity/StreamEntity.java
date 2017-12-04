package com.shootr.mobile.data.entity;

import com.shootr.mobile.domain.model.FollowableType;
import com.shootr.mobile.domain.model.SearchableType;
import java.util.List;

public class StreamEntity extends FollowableEntity implements Comparable<StreamEntity>, SearchableEntity {

    private String idStream;
    private String idUser;
    private String userName;
    private String description;
    private String topic;
    private String title;
    private String photo;
    private String landscapePhoto;
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
    private String readWriteMode;
    private Long verifiedUser;
    private Long contributorCount;
    private List<String> idUserContributors;
    private int iAmContributor;
    private int totalFollowingWatchers;
    private Boolean strategic;
    private Boolean following;
    private Boolean muted;
    private String photoIdMedia;
    private long views;

    public StreamEntity() {
        setResultType(FollowableType.STREAM);
    }

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

    public String getLandscapePhoto() {
        return landscapePhoto;
    }

    public void setLandscapePhoto(String landscapePhoto) {
        this.landscapePhoto = landscapePhoto;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public String getReadWriteMode() {
        return readWriteMode;
    }

    public void setReadWriteMode(String readWriteMode) {
        this.readWriteMode = readWriteMode;
    }

    public Long getVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(Long verifiedUser) {
        this.verifiedUser = verifiedUser;
    }

    public Long getContributorCount() {
        return contributorCount;
    }

    public void setContributorCount(Long contributorCount) {
        this.contributorCount = contributorCount;
    }

    public Boolean isStrategic() {
        return strategic;
    }

    public void setStrategic(Boolean strategic) {
        this.strategic = strategic;
    }

    public List<String> getIdUserContributors() {
        return idUserContributors;
    }

    public void setIdUserContributors(List<String> idUserContributors) {
        this.idUserContributors = idUserContributors;
    }

    public int getiAmContributor() {
        return iAmContributor;
    }

    public void setiAmContributor(int iAmContributor) {
        this.iAmContributor = iAmContributor;
    }

    @Override public String getSearcheableType() {
        return SearchableType.STREAM;
    }

    public int getTotalFollowingWatchers() {
        return totalFollowingWatchers;
    }

    public void setTotalFollowingWatchers(int totalFollowingWatchers) {
        this.totalFollowingWatchers = totalFollowingWatchers;
    }

    public Boolean isFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Boolean isMuted() {
        return muted;
    }

    public void setMuted(Boolean muted) {
        this.muted = muted;
    }

    public String getPhotoIdMedia() {
        return photoIdMedia;
    }

    public void setPhotoIdMedia(String photoIdMedia) {
        this.photoIdMedia = photoIdMedia;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }
}
