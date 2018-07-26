package com.shootr.mobile.ui.model;

import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.model.Seenable;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import timber.log.Timber;

public class UserModel implements Serializable, Cloneable, SearchableModel, PrintableModel,
    Seenable {

  private String idUser;
  private String userName;
  private String name;
  private String photo;
  private Long numFollowings;
  private Long numFollowers;
  private String website;
  private String bio;
  private String streamWatchingId;
  private String streamWatchingTitle;
  private String joinStreamDate;
  private Long joinStreamTimestamp;
  private String email;
  private Boolean emailConfirmed;
  private Boolean verifiedUser;
  private Boolean socialLogin;
  private Long createdStreamsCount;
  private Long favoritedStreamsCount;
  private boolean isStrategic;
  private boolean muted;
  private boolean isFollowing;
  private boolean isMe;
  private String shareLink;
  private String timelineGroup;
  private Date deleted;
  private Boolean seen;

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

  public Boolean isEmailConfirmed() {
    return emailConfirmed;
  }

  public void setEmailConfirmed(Boolean emailConfirmed) {
    this.emailConfirmed = emailConfirmed;
  }

  public Boolean isSocialLogin() {
    return socialLogin;
  }

  public void setSocialLogin(Boolean socialLogin) {
    this.socialLogin = socialLogin;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public boolean isFollowing() {
    return isFollowing;
  }

  public void setFollowing(boolean following) {
    isFollowing = following;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public Long getJoinStreamTimestamp() {
    return joinStreamTimestamp;
  }

  public void setJoinStreamTimestamp(Long joinStreamTimestamp) {
    this.joinStreamTimestamp = joinStreamTimestamp;
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

  public boolean isMe() {
    return isMe;
  }

  public void setMe(boolean me) {
    isMe = me;
  }

  public String getShareLink() {
    return shareLink;
  }

  public void setShareLink(String shareLink) {
    this.shareLink = shareLink;
  }

  @Override public UserModel clone() {
    try {
      return (UserModel) super.clone();
    } catch (CloneNotSupportedException e) {
      Timber.w(e, "UserModel has thrown CloneNotSupportedException. This should never happen.");
      return this;
    }
  }

  @Override public String toString() {
    return "UserModel{" + "idUser=" + idUser + ", userName='" + userName + '\'' + '}';
  }

  public String getStreamWatchingId() {
    return streamWatchingId;
  }

  public void setStreamWatchingId(String streamWatchingId) {
    this.streamWatchingId = streamWatchingId;
  }

  public String getStreamWatchingTitle() {
    return streamWatchingTitle;
  }

  public void setStreamWatchingTitle(String streamWatchingTitle) {
    this.streamWatchingTitle = streamWatchingTitle;
  }

  public String getJoinStreamDate() {
    return joinStreamDate;
  }

  public void setJoinStreamDate(String joinStreamDate) {
    this.joinStreamDate = joinStreamDate;
  }

  public Boolean isVerifiedUser() {
    return verifiedUser;
  }

  public void setVerifiedUser(Boolean verifiedUser) {
    this.verifiedUser = verifiedUser;
  }

  @Override public String getSearchableType() {
    return SearchableType.USER;
  }

  @Override public String getTimelineGroup() {
    return timelineGroup;
  }

  @Override public void setTimelineGroup(String timelineGroup) {
    this.timelineGroup = timelineGroup;
  }

  @Override public Long getOrder() {
    return 0L;
  }

  @Override public boolean isDeleted() {
    return deleted != null;
  }

  public Date getDeleted() {
    return deleted;
  }

  public void setDeleted(Date deleted) {
    this.deleted = deleted;
  }

  @Override public Boolean getSeen() {
    return seen;
  }

  @Override public void setSeen(Boolean seen) {
    this.seen = seen == null ? true : seen;
  }

  static public class MentionComparator implements Comparator<UserModel> {
    @Override public int compare(UserModel user1, UserModel user2) {
      if (user1.isFollowing() && !user2.isFollowing()) {
        return -1;
      } else if (!user1.isFollowing() && user2.isFollowing()) {
        return 1;
      } else {
        return user1.getName().compareToIgnoreCase(user2.getName());
      }
    }
  }

  public static class OrderFieldComparator implements Comparator<PrintableModel> {

    @Override public int compare(PrintableModel s1, PrintableModel s2) {
      return s2.getOrder().compareTo(s1.getOrder());
    }
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserModel userModel = (UserModel) o;

    return getIdUser() != null ? getIdUser().equals(userModel.getIdUser())
        : userModel.getIdUser() == null;
  }

  @Override public int hashCode() {
    return getIdUser() != null ? getIdUser().hashCode() : 0;
  }
}
