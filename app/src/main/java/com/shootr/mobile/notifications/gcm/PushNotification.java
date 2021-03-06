package com.shootr.mobile.notifications.gcm;

/**
 * Represents a received Push notification from the server. It includes generic notification values for displaying a
 * notification to the user, domain parameters such as push type (Activity, Shot) and some extra parameters.
 */
public class PushNotification {

    private NotificationValues notificationValues;
    private Parameters parameters;
    private boolean silent;
    private int badgeIncrement;

    public PushNotification(NotificationValues notificationValues, Parameters parameters, boolean silent,
      int badgeIncrement) {
        this.notificationValues = notificationValues;
        this.parameters = parameters;
        this.silent = silent;
        this.badgeIncrement = badgeIncrement;
    }

    public NotificationValues getNotificationValues() {
        return notificationValues;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public boolean isSilent() {
        return silent;
    }

    public int getBadgeIncrement() {
        return badgeIncrement;
    }

    @Override public String toString() {
        return "PushNotification{" +
          "notificationValues=" + notificationValues +
          ", parameters=" + parameters +
          ", silent=" + silent +
          ", badgeIncrement=" + badgeIncrement +
          '}';
    }

    /**
     * Holds values for displaying a notification to the user.
     * Warning: This class is coupled to the JSON format received from server.
     */
    public static class NotificationValues {

        private String title;
        private String contentText;
        private String icon;
        private String tickerText;
        private String optionalLongText;

        @Override public String toString() {
            return "NotificationValues{" +
              "title='" + title + '\'' +
              ", contentText='" + contentText + '\'' +
              ", icon='" + icon + '\'' +
              ", tickerText='" + tickerText + '\'' +
              '}';
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContentText() {
            return contentText;
        }

        public void setContentText(String contentText) {
            this.contentText = contentText;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getTickerText() {
            return tickerText;
        }

        public void setTickerText(String tickerText) {
            this.tickerText = tickerText;
        }

        public String getOptionalLongText() {
            return optionalLongText;
        }

        public void setOptionalLongText(String optionalLongText) {
            this.optionalLongText = optionalLongText;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NotificationValues that = (NotificationValues) o;

            if (getTitle() != null ? !getTitle().equals(that.getTitle())
                : that.getTitle() != null) {
                return false;
            }
            if (getContentText() != null ? !getContentText().equals(that.getContentText())
                : that.getContentText() != null) {
                return false;
            }
            if (getIcon() != null ? !getIcon().equals(that.getIcon()) : that.getIcon() != null)
                return false;
            if (getTickerText() != null ? !getTickerText().equals(that.getTickerText())
                : that.getTickerText() != null) {
                return false;
            }
            return getOptionalLongText() != null ? getOptionalLongText().equals(
                that.getOptionalLongText()) : that.getOptionalLongText() == null;
        }

        @Override public int hashCode() {
            int result = getTitle() != null ? getTitle().hashCode() : 0;
            result = 31 * result + (getContentText() != null ? getContentText().hashCode() : 0);
            result = 31 * result + (getIcon() != null ? getIcon().hashCode() : 0);
            result = 31 * result + (getTickerText() != null ? getTickerText().hashCode() : 0);
            result = 31 * result + (getOptionalLongText() != null ? getOptionalLongText().hashCode()
                : 0);
            return result;
        }
    }

    /**
     * Holds parameters relative to the domain received in the push notification for internal usage.
     * Warning: This class is coupled to the JSON format received from server.
     */
    public static class Parameters {

        public static final int PUSH_TYPE_SHOT = 1;
        public static final int PUSH_TYPE_ACTIVITY = 2;
        public static final int PUSH_TYPE_STREAM = 3;
        public static final int PUSH_TYPE_PRIVATE_MESSAGE = 4;

        private int pushType;
        private String activityType;
        private String idUser;
        private String username;
        private String userPhoto;
        private String idShot;
        private String idStream;
        private String idActivity;
        private String title;
        private String idStreamHolder;
        private String streamReadWriteMode;
        private String shotType;
        private String idPoll;
        private String idTargetUser;
        private String comment;
        private String image;

        public int getPushType() {
            return pushType;
        }

        public void setPushType(int pushType) {
            this.pushType = pushType;
        }

        public String getActivityType() {
            return activityType;
        }

        public void setActivityType(String activityType) {
            this.activityType = activityType;
        }

        public String getIdUser() {
            return idUser;
        }

        public void setIdUser(String idUser) {
            this.idUser = idUser;
        }

        public String getIdShot() {
            return idShot;
        }

        public void setIdShot(String idShot) {
            this.idShot = idShot;
        }

        public String getIdStream() {
            return idStream;
        }

        public void setIdStream(String idStream) {
            this.idStream = idStream;
        }

        public String getIdActivity() {
            return idActivity;
        }

        public void setIdActivity(String idActivity) {
            this.idActivity = idActivity;
        }

        @Override public String toString() {
            return "Parameters{" +
              "pushType=" + pushType +
              ", activityType='" + activityType + '\'' +
              ", idUser='" + idUser + '\'' +
              ", idShot='" + idShot + '\'' +
              ", idStream='" + idStream + '\'' +
              ", idActivity='" + idActivity + '\'' +
              ", streamReadWriteMode='" + streamReadWriteMode + '\'' +
              '}';
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIdStreamHolder() {
            return idStreamHolder;
        }

        public void setIdStreamHolder(String idStreamHolder) {
            this.idStreamHolder = idStreamHolder;
        }

        public String getStreamReadWriteMode() {
            return streamReadWriteMode;
        }

        public void setStreamReadWriteMode(String streamReadWriteMode) {
            this.streamReadWriteMode = streamReadWriteMode;
        }

        public String getShotType() {
            return shotType;
        }

        public void setShotType(String shotType) {
            this.shotType = shotType;
        }

        public String getIdPoll() {
            return idPoll;
        }

        public void setIdPoll(String idPoll) {
            this.idPoll = idPoll;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserPhoto() {
            return userPhoto;
        }

        public void setUserPhoto(String userPhoto) {
            this.userPhoto = userPhoto;
        }

        public String getIdTargetUser() {
            return idTargetUser;
        }

        public void setIdTargetUser(String idTargetUser) {
            this.idTargetUser = idTargetUser;
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
    }
}
