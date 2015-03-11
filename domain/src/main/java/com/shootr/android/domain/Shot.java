package com.shootr.android.domain;

import java.util.Comparator;
import java.util.Date;

public class Shot {

    private Long idShot;
    private String comment;
    private String image;
    private ShotUserInfo userInfo;
    private ShotEventInfo eventInfo;
    private Date publishDate;
    private Long idQueue;

    public Long getIdShot() {
        return idShot;
    }

    public void setIdShot(Long idShot) {
        this.idShot = idShot;
    }

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

    public ShotUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(ShotUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public ShotEventInfo getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(ShotEventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Long getIdQueue() {
        return idQueue;
    }

    public void setIdQueue(Long idQueue) {
        this.idQueue = idQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shot)) return false;

        Shot shot = (Shot) o;

        if (comment != null ? !comment.equals(shot.comment) : shot.comment != null) return false;
        if (eventInfo != null ? !eventInfo.equals(shot.eventInfo) : shot.eventInfo != null) return false;
        if (idQueue != null ? !idQueue.equals(shot.idQueue) : shot.idQueue != null) return false;
        if (idShot != null ? !idShot.equals(shot.idShot) : shot.idShot != null) return false;
        if (image != null ? !image.equals(shot.image) : shot.image != null) return false;
        if (publishDate != null ? !publishDate.equals(shot.publishDate) : shot.publishDate != null) return false;
        if (userInfo != null ? !userInfo.equals(shot.userInfo) : shot.userInfo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idShot != null ? idShot.hashCode() : 0;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (userInfo != null ? userInfo.hashCode() : 0);
        result = 31 * result + (eventInfo != null ? eventInfo.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (idQueue != null ? idQueue.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Shot{" +
          "idShot=" + idShot +
          ", comment='" + comment + '\'' +
          ", image='" + image + '\'' +
          ", publishDate=" + publishDate +
          '}';
    }

    public static class ShotEventInfo {

        private Long idEvent;
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

        public Long getIdEvent() {
            return idEvent;
        }

        public void setIdEvent(Long idEvent) {
            this.idEvent = idEvent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShotEventInfo)) return false;

            ShotEventInfo that = (ShotEventInfo) o;

            if (idEvent != null ? !idEvent.equals(that.idEvent) : that.idEvent != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return idEvent != null ? idEvent.hashCode() : 0;
        }

        @Override public String toString() {
            return "ShotEventInfo{" +
              "eventTitle='" + eventTitle + '\'' +
              ", idEvent=" + idEvent +
              '}';
        }
    }

    public static class ShotUserInfo {

        private Long idUser;
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

        public Long getIdUser() {
            return idUser;
        }

        public void setIdUser(Long idUser) {
            this.idUser = idUser;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShotUserInfo)) return false;

            ShotUserInfo that = (ShotUserInfo) o;

            if (!idUser.equals(that.idUser)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return idUser.hashCode();
        }

        @Override public String toString() {
            return "ShotUserInfo{" +
              "username='" + username + '\'' +
              ", idUser=" + idUser +
              '}';
        }
    }

    public static class PublishDateComparator implements Comparator<Shot> {

        @Override public int compare(Shot s1, Shot s2) {
            return s2.getPublishDate().compareTo(s1.getPublishDate());
        }
    }
}