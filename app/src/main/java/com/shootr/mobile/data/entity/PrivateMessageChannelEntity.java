package com.shootr.mobile.data.entity;

public class PrivateMessageChannelEntity  extends Synchronized {

  private String idPrivateMessageChannel;
  private String idTargetUser;
  private String title;
  private String image;
  private Boolean read;
  private PrivateMessageEntity lastPrivateMessage;


  public String getIdPrivateMessageChannel() {
    return idPrivateMessageChannel;
  }

  public void setIdPrivateMessageChannel(String idPrivateMessageChannel) {
    this.idPrivateMessageChannel = idPrivateMessageChannel;
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

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Boolean getRead() {
    return read;
  }

  public void setRead(Boolean read) {
    this.read = read;
  }

  public PrivateMessageEntity getLastPrivateMessage() {
    return lastPrivateMessage;
  }

  public void setLastPrivateMessage(PrivateMessageEntity lastPrivateMessage) {
    this.lastPrivateMessage = lastPrivateMessage;
  }
}
