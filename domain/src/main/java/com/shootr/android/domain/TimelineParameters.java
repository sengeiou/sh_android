package com.shootr.android.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimelineParameters {

    private static final Integer DEFAULT_LIMIT = 100;
    public static final long DEFAULT_SINCE_DATE = 0L;

    private List<Long> userIds;

    private Long eventId;

    private Long eventAuthorId;

    private Long sinceDate;

    private Long maxDate;

    private boolean includeHiddenSyncTriggers;

    private Integer limit;

    private TimelineParameters() {
        /* private constructor, use builder */
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getEventAuthorId() {
        return eventAuthorId;
    }

    public Long getSinceDate() {
        return sinceDate;
    }

    public Long getMaxDate() {
        return maxDate;
    }

    public List<Long> getAllUserIds() {
        //TODO cache this
        if (getEventAuthorId() == null) {
            return getUserIds();
        } else {
            ArrayList<Long> ids = new ArrayList<>(getUserIds());
            ids.add(getEventAuthorId());
            return ids;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean includesHiddenSyncTriggers() {
        return includeHiddenSyncTriggers;
    }

    public Integer getLimit() {
        return limit;
    }

    public static class Builder {

        private TimelineParameters parameters = new TimelineParameters();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            parameters.limit = DEFAULT_LIMIT;
            parameters.sinceDate = DEFAULT_SINCE_DATE;
        }

        public Builder forUsers(List<Long> userIds) {
            parameters.userIds = userIds;
            return this;
        }

        public Builder forUsers(List<Long> userIds, Long... moreUserIds) {
            parameters.userIds = userIds;
            parameters.userIds.addAll(Arrays.asList(moreUserIds));
            return this;
        }

        public Builder forUsers(Long... userIds) {
            parameters.userIds = Arrays.asList(userIds);
            return this;
        }

        public Builder forEvent(Long eventId, Long eventAuthorId) {
            parameters.eventId = eventId;
            parameters.eventAuthorId = eventAuthorId;
            return this;
        }

        public Builder forEvent(Event event) {
            parameters.eventId = event.getId();
            parameters.eventAuthorId = event.getAuthorId();
            return this;
        }

        public Builder forActivity() {
            parameters.eventId = null;
            parameters.eventAuthorId = null;
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

        public Builder includeSyncTriggers(boolean includeHiddenSyncTriggers) {
            parameters.includeHiddenSyncTriggers = includeHiddenSyncTriggers;
            return this;
        }

        public TimelineParameters build() {
            if (parameters.getUserIds() == null || parameters.getUserIds().isEmpty()) {
                throw new IllegalArgumentException("User list in TimelineParameters must not be null or empty");
            }
            return parameters;
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimelineParameters that = (TimelineParameters) o;

        if (includeHiddenSyncTriggers != that.includeHiddenSyncTriggers) return false;
        if (!userIds.equals(that.userIds)) return false;
        if (eventId != null ? !eventId.equals(that.eventId) : that.eventId != null) return false;
        if (eventAuthorId != null ? !eventAuthorId.equals(that.eventAuthorId) : that.eventAuthorId != null)
            return false;
        if (!sinceDate.equals(that.sinceDate)) return false;
        if (maxDate != null ? !maxDate.equals(that.maxDate) : that.maxDate != null) return false;
        return limit.equals(that.limit);

    }

    @Override public int hashCode() {
        int result = userIds.hashCode();
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        result = 31 * result + (eventAuthorId != null ? eventAuthorId.hashCode() : 0);
        result = 31 * result + sinceDate.hashCode();
        result = 31 * result + (maxDate != null ? maxDate.hashCode() : 0);
        result = 31 * result + (includeHiddenSyncTriggers ? 1 : 0);
        result = 31 * result + limit.hashCode();
        return result;
    }

    @Override public String toString() {
        return "TimelineParameters{" +
                "userIds=" + userIds +
                ", eventId=" + eventId +
                ", eventAuthorId=" + eventAuthorId +
                ", sinceDate=" + sinceDate +
                ", maxDate=" + maxDate +
                ", includeHiddenSyncTriggers=" + includeHiddenSyncTriggers +
                ", limit=" + limit +
                '}';
    }
}
