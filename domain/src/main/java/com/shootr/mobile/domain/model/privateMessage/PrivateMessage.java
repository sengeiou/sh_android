package com.shootr.mobile.domain.model.privateMessage;

import com.shootr.mobile.domain.model.shot.BaseMessage;
import java.util.Comparator;
import java.util.Date;

public class PrivateMessage extends BaseMessage {

  private String idPrivateMessage;
  private String idPrivateMessageChannel;
  private String idTargetUser;
  private Date publishDate;
  private Long idQueue;

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

  @Override public Long getIdQueue() {
    return idQueue;
  }

  public void setIdQueue(Long idQueue) {
    this.idQueue = idQueue;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  public String getIdTargetUser() {
    return idTargetUser;
  }

  public void setIdTargetUser(String idTargetUser) {
    this.idTargetUser = idTargetUser;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PrivateMessage)) return false;

    PrivateMessage baseMessage = (PrivateMessage) o;

    if (idPrivateMessage != null ? !idPrivateMessage.equals(baseMessage.idPrivateMessage)
        : baseMessage.idPrivateMessage != null) {
      return false;
    }
    if (getComment() != null ? !getComment().equals(baseMessage.getComment())
        : baseMessage.getComment() != null) {
      return false;
    }
    if (getImage() != null ? !getImage().equals(baseMessage.getImage())
        : baseMessage.getImage() != null) {
      return false;
    }
    if (getUserInfo() != null ? !getUserInfo().equals(baseMessage.getUserInfo())
        : baseMessage.getUserInfo() != null) {
      return false;
    }

    if (getIdQueue() != null ? !getIdQueue().equals(baseMessage.getIdQueue())
        : baseMessage.getIdQueue() != null) {
      return false;
    }

    if (getVideoUrl() != null ? !getVideoUrl().equals(baseMessage.getVideoUrl())
        : baseMessage.getVideoUrl() != null) {
      return false;
    }
    if (getVideoTitle() != null ? !getVideoTitle().equals(baseMessage.getVideoTitle())
        : baseMessage.getVideoTitle() != null) {
      return false;
    }
    if (getVideoDuration() != null ? !getVideoDuration().equals(baseMessage.getVideoDuration())
        : baseMessage.getVideoDuration() != null) {
      return false;
    }

    return !(getMetadata() != null ? !getMetadata().equals(baseMessage.getMetadata())
        : baseMessage.getMetadata() != null);
  }

  @Override public int hashCode() {
    int result = idPrivateMessage != null ? idPrivateMessage.hashCode() : 0;
    result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
    result = 31 * result + (getImage() != null ? getImage().hashCode() : 0);
    result = 31 * result + (getUserInfo() != null ? getUserInfo().hashCode() : 0);
    result = 31 * result + (getIdQueue() != null ? getIdQueue().hashCode() : 0);
    result = 31 * result + (getVideoUrl() != null ? getVideoUrl().hashCode() : 0);
    result = 31 * result + (getVideoTitle() != null ? getVideoTitle().hashCode() : 0);
    result = 31 * result + (getVideoDuration() != null ? getVideoDuration().hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "PrivateMessage{" +
        "idPrivateMessage=" + idPrivateMessage +
        ", comment='" + getComment() + '\'' +
        ", image='" + getImage() +
        '}';
  }

  @Override public String getResultType() {
    return "PRIVATE_MESSAGE";
  }

  @Override public Long getOrder() {
    return 0L;
  }

  @Override public Date getDeletedData() {
    return getMetadata().getDeleted();
  }

  @Override public void setDeletedData(Date deleted) {
    //TODO
  }

  @Override public String getMessageType() {
    return null;
  }

  public static class NewerAboveComparator implements Comparator<PrivateMessage> {

    @Override public int compare(PrivateMessage s1, PrivateMessage s2) {
      return s2.getPublishDate().compareTo(s1.getPublishDate());
    }
  }

  public static class NewerBelowComparator implements Comparator<PrivateMessage> {

    @Override public int compare(PrivateMessage s1, PrivateMessage s2) {
      return s1.getPublishDate().compareTo(s2.getPublishDate());
    }
  }
}