package com.shootr.android.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventTimelineParameters extends TimelineParameters {

    private String eventAuthorId;

    private String eventId;

    private String shotType;

    private Integer maxNiceShotsIncluded;

    private Boolean includeNiceShots;

    private EventTimelineParameters() {
        /* private constructor, use builder */
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventAuthorId() {
        return eventAuthorId;
    }

    public String getShotType() {
        return shotType;
    }

    @Override
    public List<String> getUserIds() {
        ArrayList<String> ids = new ArrayList<>(super.getUserIds());
        ids.add(getEventAuthorId());
        return ids;
    }

    public Integer getMaxNiceShotsIncluded() {
        return maxNiceShotsIncluded;
    }

    public void setMaxNiceShotsIncluded(Integer maxNiceShotsIncluded) {
        this.maxNiceShotsIncluded = maxNiceShotsIncluded;
    }

    public Boolean getIncludeNiceShots() {
        return includeNiceShots;
    }

    public void setIncludeNiceShots(Boolean includeNiceShots) {
        this.includeNiceShots = includeNiceShots;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventTimelineParameters)) return false;

        EventTimelineParameters that = (EventTimelineParameters) o;

        if (userIds != null ? !userIds.equals(that.userIds) : that.userIds != null) return false;
        if (eventAuthorId != null ? !eventAuthorId.equals(that.eventAuthorId) : that.eventAuthorId != null)
            return false;
        if (eventId != null ? !eventId.equals(that.eventId) : that.eventId != null) return false;
        if (shotType != null ? !shotType.equals(that.shotType) : that.shotType != null) return false;
        if (sinceDate != null ? !sinceDate.equals(that.sinceDate) : that.sinceDate != null) return false;
        if (maxDate != null ? !maxDate.equals(that.maxDate) : that.maxDate != null) return false;
        return !(limit != null ? !limit.equals(that.limit) : that.limit != null);
    }

    @Override
    public int hashCode() {
        int result = userIds != null ? userIds.hashCode() : 0;
        result = 31 * result + (eventAuthorId != null ? eventAuthorId.hashCode() : 0);
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        result = 31 * result + (shotType != null ? shotType.hashCode() : 0);
        result = 31 * result + (sinceDate != null ? sinceDate.hashCode() : 0);
        result = 31 * result + (maxDate != null ? maxDate.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventTimelineParameters{" +
          "userIds=" + userIds +
          ", eventAuthorId='" + eventAuthorId + '\'' +
          ", eventId='" + eventId + '\'' +
          ", shotType='" + shotType + '\'' +
          ", sinceDate=" + sinceDate +
          ", maxDate=" + maxDate +
          ", limit=" + limit +
          '}';
    }

    public static class Builder {

        private EventTimelineParameters parameters = new EventTimelineParameters();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            parameters.limit = DEFAULT_LIMIT;
            parameters.sinceDate = DEFAULT_SINCE_DATE;
            parameters.shotType = ShotType.COMMENT;
        }

        public Builder forUsers(List<String> userIds) {
            parameters.userIds = userIds;
            return this;
        }

        public Builder forUsers(List<String> userIds, String moreUserIds) {
            parameters.userIds = userIds;
            parameters.userIds.addAll(Arrays.asList(moreUserIds));
            return this;
        }

        public Builder forUsers(String... userIds) {
            parameters.userIds = Arrays.asList(userIds);
            return this;
        }

        public Builder forEvent(String eventId, String eventAuthorId) {
            parameters.eventId = eventId;
            parameters.eventAuthorId = eventAuthorId;
            return this;
        }

        public Builder forEvent(Event event) {
            parameters.eventId = event.getId();
            parameters.eventAuthorId = event.getAuthorId();
            return this;
        }

        public Builder since(Long sinceDate) {
            parameters.sinceDate = sinceDate;
            return this;
        }

        public Builder maxDate(Long maxDate) {
            parameters.maxDate = maxDate;
            return this;
        }

        public Builder niceShots(Integer maxNice) {
            parameters.setIncludeNiceShots(maxNice > 0);
            parameters.setMaxNiceShotsIncluded(maxNice);
            return this;
        }

        public EventTimelineParameters build() {
            if (parameters.getUserIds() == null || parameters.getUserIds().isEmpty()) {
                throw new IllegalArgumentException("User list in TimelineParameters must not be null or empty");
            }
            return parameters;
        }
    }


}
