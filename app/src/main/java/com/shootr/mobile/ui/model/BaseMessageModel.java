package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.Date;

public class BaseMessageModel implements Serializable {

  private String idUser;
  private String username;
  private String avatar;
  private boolean verifiedUser;
  private Date birth;
  private String comment;
  private ShotImageModel image;
  private String videoUrl;
  private String videoTitle;
  private String videoDuration;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public ShotImageModel getImage() {
    return image;
  }

  public void setImage(ShotImageModel image) {
    this.image = image;
  }

  public boolean hasVideo() {
    return videoUrl != null;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public String getVideoTitle() {
    return videoTitle;
  }

  public void setVideoTitle(String videoTitle) {
    this.videoTitle = videoTitle;
  }

  public String getVideoDuration() {
    return videoDuration;
  }

  public void setVideoDuration(String videoDuration) {
    this.videoDuration = videoDuration;
  }

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public boolean isVerifiedUser() {
    return verifiedUser;
  }

  public void setVerifiedUser(boolean verifiedUser) {
    this.verifiedUser = verifiedUser;
  }

  public Date getBirth() {
    return birth;
  }

  public void setBirth(Date birth) {
    this.birth = birth;
  }
}