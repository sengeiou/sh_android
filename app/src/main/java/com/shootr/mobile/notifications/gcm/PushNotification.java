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

    public PushNotification(NotificationValues notificationValues,
      Parameters parameters,
      boolean silent,
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

    @Override
    public String toString() {
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

        @Override
        public String toString() {
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
    }

    /**
     * Holds parameters relative to the domain received in the push notification for internal usage.
     * Warning: This class is coupled to the JSON format received from server.
     */
    public static class Parameters {

        public static final int PUSH_TYPE_SHOT = 1;
        public static final int PUSH_TYPE_ACTIVITY = 2;

        private int pushType;
        private String activityType;
        private String idUser;
        private String idShot;
        private String idStream;
        private String idActivity;

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

        @Override
        public String toString() {
            return "Parameters{" +
              "pushType=" + pushType +
              ", activityType='" + activityType + '\'' +
              ", idUser='" + idUser + '\'' +
              ", idShot='" + idShot + '\'' +
              ", idStream='" + idStream + '\'' +
              ", idActivity='" + idActivity + '\'' +
              '}';
        }
    }
}
