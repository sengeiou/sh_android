package com.shootr.mobile.domain.model.activity;

import com.shootr.mobile.domain.model.shot.Shot;
import java.util.Comparator;
import java.util.Date;

public class Activity {

  private String idActivity;
  private String idUser;
  private String idTargetUser;
  private String username;
  private String idStream;
  private String streamTitle;
  private String comment;
  private String type;
  private Date publishDate;
  private Shot shot;
  private String idAuthorStream;
  private String idPoll;
  private String pollQuestion;
  private String pollOptionText;

  private ActivityUserInfo userInfo;

  private ActivityStreamInfo streamInfo;

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  public ActivityUserInfo getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(ActivityUserInfo userInfo) {
    this.userInfo = userInfo;
  }

  public String getIdTargetUser() {
    return idTargetUser;
  }

  public void setIdTargetUser(String idTargetUser) {
    this.idTargetUser = idTargetUser;
  }

  public ActivityStreamInfo getStreamInfo() {
    return streamInfo;
  }

  public void setStreamInfo(ActivityStreamInfo streamInfo) {
    this.streamInfo = streamInfo;
  }

  public String getIdActivity() {
    return idActivity;
  }

  public void setIdActivity(String idActivity) {
    this.idActivity = idActivity;
  }

  public String getIdPoll() {
    return idPoll;
  }

  public void setIdPoll(String idPoll) {
    this.idPoll = idPoll;
  }

  public String getPollQuestion() {
    return pollQuestion;
  }

  public void setPollQuestion(String pollQuestion) {
    this.pollQuestion = pollQuestion;
  }

  public String getPollOptionText() {
    return pollOptionText;
  }

  public void setPollOptionText(String pollOptionText) {
    this.pollOptionText = pollOptionText;
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

  public String getIdStream() {
    return idStream;
  }

  public void setIdStream(String idStream) {
    this.idStream = idStream;
  }

  public String getStreamTitle() {
    return streamTitle;
  }

  public void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override public String toString() {
    return "Activity{" +
        "idActivity='" + idActivity + '\'' +
        ", idUser='" + idUser + '\'' +
        ", username='" + username + '\'' +
        ", idStream='" + idStream + '\'' +
        ", streamTitle='" + streamTitle + '\'' +
        ", comment='" + comment + '\'' +
        ", type='" + type + '\'' +
        ", publishDate=" + publishDate +
        ", userInfo=" + userInfo +
        ", streamInfo=" + streamInfo +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Activity)) return false;

    Activity activity = (Activity) o;

    if (idActivity != null ? !idActivity.equals(activity.idActivity)
        : activity.idActivity != null) {
      return false;
    }
    if (idUser != null ? !idUser.equals(activity.idUser) : activity.idUser != null) return false;
    if (username != null ? !username.equals(activity.username) : activity.username != null) {
      return false;
    }
    if (idStream != null ? !idStream.equals(activity.idStream) : activity.idStream != null) {
      return false;
    }
    if (streamTitle != null ? !streamTitle.equals(activity.streamTitle)
        : activity.streamTitle != null) {
      return false;
    }
    if (comment != null ? !comment.equals(activity.comment) : activity.comment != null) {
      return false;
    }
    if (type != null ? !type.equals(activity.type) : activity.type != null) return false;
    if (publishDate != null ? !publishDate.equals(activity.publishDate)
        : activity.publishDate != null) {
      return false;
    }
    if (userInfo != null ? !userInfo.equals(activity.userInfo) : activity.userInfo != null) {
      return false;
    }
    return !(streamInfo != null ? !streamInfo.equals(activity.streamInfo)
        : activity.streamInfo != null);
  }

  @Override public int hashCode() {
    int result = idActivity != null ? idActivity.hashCode() : 0;
    result = 31 * result + (idUser != null ? idUser.hashCode() : 0);
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (idStream != null ? idStream.hashCode() : 0);
    result = 31 * result + (streamTitle != null ? streamTitle.hashCode() : 0);
    result = 31 * result + (comment != null ? comment.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
    result = 31 * result + (userInfo != null ? userInfo.hashCode() : 0);
    result = 31 * result + (streamInfo != null ? streamInfo.hashCode() : 0);
    return result;
  }

  public Shot getShot() {
    return shot;
  }

  public void setShot(Shot shot) {
    this.shot = shot;
  }

  public String getIdAuthorStream() {
    return idAuthorStream;
  }

  public void setIdAuthorStream(String idAuthorStream) {
    this.idAuthorStream = idAuthorStream;
  }

  public static class ActivityUserInfo {

    private String idUser;
    private String username;
    private String avatar;
    private boolean strategic;

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

    public boolean isStrategic() {
      return strategic;
    }

    public void setStrategic(boolean strategic) {
      this.strategic = strategic;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ActivityUserInfo)) return false;

      ActivityUserInfo that = (ActivityUserInfo) o;

      if (!idUser.equals(that.idUser)) return false;

      return true;
    }

    @Override public int hashCode() {
      return idUser.hashCode();
    }

    @Override public String toString() {
      return "ActivityUserInfo{" +
          "username='" + username + '\'' +
          ", idUser=" + idUser +
          '}';
    }
  }

  public static class ActivityStreamInfo {

    private String idStream;
    private String streamTitle;
    private String idUser;
    private boolean strategic;
    private boolean isVerified;

    public String getStreamTitle() {
      return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
      this.streamTitle = streamTitle;
    }

    public String getIdStream() {
      return idStream;
    }

    public void setIdStream(String idStream) {
      this.idStream = idStream;
    }

    public boolean isStrategic() {
      return strategic;
    }

    public void setStrategic(boolean strategic) {
      this.strategic = strategic;
    }

    public boolean isVerified() {
      return isVerified;
    }

    public void setVerified(boolean verified) {
      isVerified = verified;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ActivityStreamInfo)) return false;

      ActivityStreamInfo that = (ActivityStreamInfo) o;

      if (idStream != null ? !idStream.equals(that.idStream) : that.idStream != null) return false;

      return true;
    }

    @Override public int hashCode() {
      return idStream != null ? idStream.hashCode() : 0;
    }

    @Override public String toString() {
      return "ActivityStreamInfo{" +
          "streamTitle='" + streamTitle + '\'' +
          ", idStream=" + idStream +
          '}';
    }

    public String getIdUser() {
      return idUser;
    }

    public void setIdUser(String idUser) {
      this.idUser = idUser;
    }
  }

  public static class NewerAboveComparator implements Comparator<Activity> {

    @Override public int compare(Activity a1, Activity a2) {
      return a2.getPublishDate().compareTo(a1.getPublishDate());
    }
  }
}
