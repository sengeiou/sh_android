package com.shootr.mobile.ui.model;

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

  public Long getJoinStreamTimestamp() {
    return joinStreamTimestamp;
  }

  public void setJoinStreamTimestamp(Long joinStreamTimestamp) {
    this.joinStreamTimestamp = joinStreamTimestamp;
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
    return "UserModel{" +
        "idUser=" + idUser +
        ", userName='" + userName + '\'' +
        '}';
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
}
