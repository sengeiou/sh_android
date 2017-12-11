package com.shootr.mobile.ui.model;

import com.shootr.mobile.domain.model.SearchableType;
import java.io.Serializable;

public class StreamModel implements Serializable, SearchableModel {

    private String idStream;
    private String authorId;
    private String authorUsername;
    private String title;
    private String picture;
    private String landscapePicture;
    private String description;
    private String topic;
    private boolean amIAuthor;
    private Integer mediaCount;
    private Boolean removed;
    private Integer totalFollowers;
    private Integer totalWatchers;
    private Long historicWatchers;
    private Long totalShots;
    private Long uniqueShots;
    private Integer readWriteMode;
    private boolean verifiedUser;
    private Long contributorCount;
    private boolean isCurrentUserContributor;
    private boolean isFollowing;
    private int totalFollowingWatchers;
    private boolean isStrategic;
    private boolean muted;
    private long views;
    private int position;
    private boolean showRankPosition;

    public Boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override public int hashCode() {
        return idStream.hashCode();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLandscapePicture() {
        return landscapePicture;
    }

    public void setLandscapePicture(String landscapePicture) {
        this.landscapePicture = landscapePicture;
    }

    public boolean amIAuthor() {
        return amIAuthor;
    }

    public void setAmIAuthor(boolean amIAuthor) {
        this.amIAuthor = amIAuthor;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public Integer getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(Integer totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public Integer getTotalWatchers() {
        return totalWatchers;
    }

    public void setTotalWatchers(Integer totalWatchers) {
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

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StreamModel that = (StreamModel) o;

        return idStream.equals(that.idStream);
    }

    public Integer getReadWriteMode() {
        return readWriteMode;
    }

    public void setReadWriteMode(Integer readWriteMode) {
        this.readWriteMode = readWriteMode;
    }

    public boolean isVerifiedUser() {
        return verifiedUser;
    }

    public void setVerifiedUser(boolean verifiedUser) {
        this.verifiedUser = verifiedUser;
    }

    public Long getContributorCount() {
        return contributorCount;
    }

    public void setContributorCount(Long contributorCount) {
        this.contributorCount = contributorCount;
    }

    public boolean isCurrentUserContributor() {
        return isCurrentUserContributor;
    }

    public void setCurrentUserContributor(boolean currentUserContributor) {
        isCurrentUserContributor = currentUserContributor;
    }

    public boolean isStrategic() {
        return isStrategic;
    }

    public void setStrategic(boolean strategic) {
        isStrategic = strategic;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    @Override public String getSearchableType() {
        return SearchableType.STREAM;
    }

    public int getTotalFollowingWatchers() {
        return totalFollowingWatchers;
    }

    public void setTotalFollowingWatchers(int totalFollowingWatchers) {
        this.totalFollowingWatchers = totalFollowingWatchers;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isShowRankPosition() {
        return showRankPosition;
    }

    public void setShowRankPosition(boolean showRankPosition) {
        this.showRankPosition = showRankPosition;
    }
}
