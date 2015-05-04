package com.shootr.android.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimelineParameters {

    private static final Integer DEFAULT_LIMIT = 100;
    public static final Long DEFAULT_SINCE_DATE = 0L;

    private List<String> userIds;

    private String eventId;

    private String eventAuthorId;

    private Long sinceDate;

    private Long maxDate;

    private boolean includeHiddenSyncTriggers;

    private Integer limit;

    private TimelineParameters() {
        /* private constructor, use builder */
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventAuthorId() {
        return eventAuthorId;
    }

    public Long getSinceDate() {
        return sinceDate;
    }

    public Long getMaxDate() {
        return maxDate;
    }

    public List<String> getAllUserIds() {
        //TODO cache this
        if (getEventAuthorId() == null) {
            return getUserIds();
        } else {
            ArrayList<String> ids = new ArrayList<>(getUserIds());
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

        public Builder includeSyncTriggers() {
            parameters.includeHiddenSyncTriggers = true;
            return this;
        }

        public TimelineParameters build() {
            if (parameters.getUserIds() == null || parameters.getUserIds().isEmpty()) {
                throw new IllegalArgumentException("User list in TimelineParameters must not be null or empty");
            }
            return parameters;
        }
    }
}
