package com.shootr.mobile.ui.model;

public class PrivateMessageChannelModel {

  private String idPrivateMessageChannel;
  private String idUser;
  private String idTargetUser;
  private String title;
  private String imageUrl;
  private Boolean read;
  private Long lastMessageTime;
  private String lastMessageComment;

  public String getIdPrivateMessageChannel() {
    return idPrivateMessageChannel;
  }

  public void setIdPrivateMessageChannel(String idPrivateMessageChannel) {
    this.idPrivateMessageChannel = idPrivateMessageChannel;
  }

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getIdTargetUser() {
    return idTargetUser;
  }

  public void setIdTargetUser(String idTargetUser) {
    this.idTargetUser = idTargetUser;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Boolean getRead() {
    return read;
  }

  public void setRead(Boolean read) {
    this.read = read;
  }

  public Long getLastMessageTime() {
    return lastMessageTime;
  }

  public void setLastMessageTime(Long lastMessageTime) {
    this.lastMessageTime = lastMessageTime;
  }

  public String getLastMessageComment() {
    return lastMessageComment;
  }

  public void setLastMessageComment(String lastMessageComment) {
    this.lastMessageComment = lastMessageComment;
  }
}

