package com.shootr.mobile.data.api.entity;


public class BaseMessageApiEntity {

  private String comment;
  private String image;
  private Long imageWidth;
  private Long imageHeight;
  private String videoUrl;
  private String videoTitle;
  private Long videoDuration;
  private BaseMessageEntitiesApiEntity entities;

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

  public BaseMessageEntitiesApiEntity getEntities() {
    return entities;
  }

  public void setEntities(BaseMessageEntitiesApiEntity entities) {
    this.entities = entities;
  }
}
