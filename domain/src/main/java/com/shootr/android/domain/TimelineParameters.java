package com.shootr.android.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TimelineParameters {

    private List<Long> userIds;

    private Long eventId;

    private Long eventAuthorId;

    private Long sinceDate;

    private Long maxDate;

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

    public static class Builder {

        private TimelineParameters parameters = new TimelineParameters();

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

        public Builder since(Long sinceDate) {
            parameters.sinceDate = sinceDate;
            return this;
        }

        public Builder maxDate(Long maxDate) {
            parameters.maxDate = maxDate;
            return this;
        }

        public TimelineParameters build() {
            if (parameters.getUserIds() == null || parameters.getUserIds().isEmpty()) {
                throw new IllegalArgumentException("User list in TimelineParameters must not be null or empty");
            }
            if (parameters.getSinceDate() == null) {
                parameters.sinceDate = 0L;
            }
            return parameters;
        }
    }
}
