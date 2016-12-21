package com.shootr.mobile.ui.model;

import java.util.Date;

public class PrivateMessageModel extends BaseMessageModel {

  private String idPrivateMessage;
  private String idPrivateMessageChannel;
  private String idTargetUser;
  private Date publishDate;

  public String getIdPrivateMessage() {
    return idPrivateMessage;
  }

  public void setIdPrivateMessage(String idPrivateMessage) {
    this.idPrivateMessage = idPrivateMessage;
  }

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

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PrivateMessageModel that = (PrivateMessageModel) o;

    if (idPrivateMessage != null ? !idPrivateMessage.equals(that.idPrivateMessage)
        : that.idPrivateMessage != null) {
      return false;
    }
    if (idPrivateMessageChannel != null ? !idPrivateMessageChannel.equals(
        that.idPrivateMessageChannel) : that.idPrivateMessageChannel != null) {
      return false;
    }
    if (idTargetUser != null ? !idTargetUser.equals(that.idTargetUser)
        : that.idTargetUser != null) {
      return false;
    }
    return publishDate != null ? publishDate.equals(that.publishDate) : that.publishDate == null;
  }

  @Override public int hashCode() {
    int result = idPrivateMessage != null ? idPrivateMessage.hashCode() : 0;
    result =
        31 * result + (idPrivateMessageChannel != null ? idPrivateMessageChannel.hashCode() : 0);
    result = 31 * result + (idTargetUser != null ? idTargetUser.hashCode() : 0);
    result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
    return result;
  }
}
