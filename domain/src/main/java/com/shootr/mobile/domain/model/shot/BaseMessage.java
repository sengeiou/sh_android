package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.model.EntityMetadata;
import com.shootr.mobile.domain.model.PrintableItem;
import java.util.ArrayList;

public abstract class BaseMessage implements Sendable, PrintableItem {

  private String comment;
  private String image;
  private Long imageWidth;
  private Long imageHeight;
  private String videoUrl;
  private String videoTitle;
  private Long videoDuration;
  private BaseMessageUserInfo userInfo;
  private EntityMetadata metadata;
  private Entities entities;
  private String imageIdMedia;
  private ArrayList<String> timelineFlags;
  private ArrayList<String> detailFlags;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Long getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(Long imageWidth) {
    this.imageWidth = imageWidth;
  }

  public Long getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(Long imageHeight) {
    this.imageHeight = imageHeight;
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

  public Long getVideoDuration() {
    return videoDuration;
  }

  public void setVideoDuration(Long videoDuration) {
    this.videoDuration = videoDuration;
  }

  public boolean hasVideoEmbed() {
    return videoUrl != null;
  }

  public BaseMessageUserInfo getUserInfo() {
    return userInfo;
  }

  public EntityMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(EntityMetadata metadata) {
    this.metadata = metadata;
  }

  public void setUserInfo(BaseMessageUserInfo userInfo) {
    this.userInfo = userInfo;
  }

  public static class BaseMessageUserInfo {

    private String idUser;
    private String username;
    private String avatar;
    private Long verifiedUser;

    public String getAvatar() {
      return avatar;
    }

    public void setAvatar(String avatar) {
      this.avatar = avatar;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getIdUser() {
      return idUser;
    }

    public void setIdUser(String idUser) {
      this.idUser = idUser;
    }

    public Long getVerifiedUser() {
      return verifiedUser;
    }

    public void setVerifiedUser(Long verifiedUser) {
      this.verifiedUser = verifiedUser;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BaseMessageUserInfo)) return false;

      BaseMessageUserInfo that = (BaseMessageUserInfo) o;

      if (!idUser.equals(that.idUser)) return false;

      return true;
    }

    @Override public int hashCode() {
      return idUser.hashCode();
    }

    @Override public String toString() {
      return "BaseMessageUserInfo{" +
          "username='" + username + '\'' +
          ", idUser=" + idUser +
          '}';
    }
  }

  public Entities getEntities() {
    return entities;
  }

  public void setEntities(Entities entities) {
    this.entities = entities;
  }

  public String getImageIdMedia() {
    return imageIdMedia;
  }

  public void setImageIdMedia(String imageIdMedia) {
    this.imageIdMedia = imageIdMedia;
  }

  public ArrayList<String> getTimelineFlags() {
    return timelineFlags;
  }

  public void setTimelineFlags(ArrayList<String> timelineFlags) {
    this.timelineFlags = timelineFlags;
  }

  public ArrayList<String> getDetailFlags() {
    return detailFlags;
  }

  public void setDetailFlags(ArrayList<String> detailFlags) {
    this.detailFlags = detailFlags;
  }
}
