package com.shootr.android.domain;

import java.util.Comparator;
import java.util.Date;

public class Activity {

    private String idActivity;
    private String idUser;
    private String username;
    private String idEvent;
    private String eventTitle;
    private String eventTag;
    private String comment;
    private String type;
    private Date publishDate;

    private ActivityUserInfo userInfo;

    private ActivityEventInfo eventInfo;

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

    public ActivityEventInfo getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(ActivityEventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
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

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
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
          ", idEvent='" + idEvent + '\'' +
          ", eventTitle='" + eventTitle + '\'' +
          ", eventTag='" + eventTag + '\'' +
          ", comment='" + comment + '\'' +
          ", type='" + type + '\'' +
          ", publishDate=" + publishDate +
          ", userInfo=" + userInfo +
          ", eventInfo=" + eventInfo +
          '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;

        Activity activity = (Activity) o;

        if (idActivity != null ? !idActivity.equals(activity.idActivity) : activity.idActivity != null) return false;
        if (idUser != null ? !idUser.equals(activity.idUser) : activity.idUser != null) return false;
        if (username != null ? !username.equals(activity.username) : activity.username != null) return false;
        if (idEvent != null ? !idEvent.equals(activity.idEvent) : activity.idEvent != null) return false;
        if (eventTitle != null ? !eventTitle.equals(activity.eventTitle) : activity.eventTitle != null) return false;
        if (eventTag != null ? !eventTag.equals(activity.eventTag) : activity.eventTag != null) return false;
        if (comment != null ? !comment.equals(activity.comment) : activity.comment != null) return false;
        if (type != null ? !type.equals(activity.type) : activity.type != null) return false;
        if (publishDate != null ? !publishDate.equals(activity.publishDate) : activity.publishDate != null)
            return false;
        if (userInfo != null ? !userInfo.equals(activity.userInfo) : activity.userInfo != null) return false;
        return !(eventInfo != null ? !eventInfo.equals(activity.eventInfo) : activity.eventInfo != null);
    }

    @Override public int hashCode() {
        int result = idActivity != null ? idActivity.hashCode() : 0;
        result = 31 * result + (idUser != null ? idUser.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (idEvent != null ? idEvent.hashCode() : 0);
        result = 31 * result + (eventTitle != null ? eventTitle.hashCode() : 0);
        result = 31 * result + (eventTag != null ? eventTag.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (userInfo != null ? userInfo.hashCode() : 0);
        result = 31 * result + (eventInfo != null ? eventInfo.hashCode() : 0);
        return result;
    }

    public static class ActivityUserInfo {

        private String idUser;
        private String username;
        private String avatar;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ActivityUserInfo)) return false;

            ActivityUserInfo that = (ActivityUserInfo) o;

            if (!idUser.equals(that.idUser)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return idUser.hashCode();
        }

        @Override public String toString() {
            return "ActivityUserInfo{" +
              "username='" + username + '\'' +
              ", idUser=" + idUser +
              '}';
        }
    }

    public static class ActivityEventInfo {

        private String idEvent;
        private String eventTitle;
        private String eventTag;

        public String getEventTag() {
            return eventTag;
        }

        public void setEventTag(String eventTag) {
            this.eventTag = eventTag;
        }

        public String getEventTitle() {
            return eventTitle;
        }

        public void setEventTitle(String eventTitle) {
            this.eventTitle = eventTitle;
        }

        public String getIdEvent() {
            return idEvent;
        }

        public void setIdEvent(String idEvent) {
            this.idEvent = idEvent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ActivityEventInfo)) return false;

            ActivityEventInfo that = (ActivityEventInfo) o;

            if (idEvent != null ? !idEvent.equals(that.idEvent) : that.idEvent != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return idEvent != null ? idEvent.hashCode() : 0;
        }

        @Override public String toString() {
            return "ActivityEventInfo{" +
              "eventTitle='" + eventTitle + '\'' +
              ", idEvent=" + idEvent +
              '}';
        }
    }

    public static class NewerAboveComparator implements Comparator<Activity> {

        @Override public int compare(Activity a1, Activity a2) {
            return a2.getPublishDate().compareTo(a1.getPublishDate());
        }
    }
}
