package com.shootr.android.domain;

public class Activity {

    private String idActivity;
    private String idUser;
    private String username;
    private String idEvent;
    private String eventTitle;
    private String eventTag;
    private String comment;
    private String type;

    private ActivityUserInfo userInfo;
    private ActivityEventInfo eventInfo;

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
}
