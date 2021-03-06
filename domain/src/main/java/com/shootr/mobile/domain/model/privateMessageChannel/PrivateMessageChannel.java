package com.shootr.mobile.domain.model.privateMessageChannel;

public class PrivateMessageChannel {

  private String idPrivateMessageChanel;
  private String idTargetUser;
  private String title;
  private String image;
  private boolean read;
  private Long lastMessageTime;
  private String lastMessageComment;
  private boolean muted;
  private boolean followingTargetUser;

  public String getIdPrivateMessageChanel() {
    return idPrivateMessageChanel;
  }

  public void setIdPrivateMessageChanel(String idPrivatseMessageChanel) {
    this.idPrivateMessageChanel = idPrivatseMessageChanel;
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

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
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

  public boolean isMuted() {
    return muted;
  }

  public void setMuted(boolean muted) {
    this.muted = muted;
  }

  public boolean isFollowingTargetUser() {
    return followingTargetUser;
  }

  public void setFollowingTargetUser(boolean followingTargetUser) {
    this.followingTargetUser = followingTargetUser;
  }
}
