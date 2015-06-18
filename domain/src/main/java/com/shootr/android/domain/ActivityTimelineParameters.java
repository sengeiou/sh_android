package com.shootr.android.domain;

import java.util.Arrays;
import java.util.List;

public class ActivityTimelineParameters extends TimelineParameters {

    private List<String> includedTypes;
    private String excludedType;
    private List<String> userIds;
    private String currentUserId;

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public List<String> getIncludedTypes() {
        return includedTypes;
    }

    public String getExcludedType() {
        return excludedType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public static class Builder {

        private ActivityTimelineParameters parameters = new ActivityTimelineParameters();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            parameters.limit = DEFAULT_LIMIT;
            parameters.sinceDate = DEFAULT_SINCE_DATE;
            parameters.excludedType = ActivityType.COMMENT;
            parameters.includedTypes = allKnownActivityTypes();
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

        public Builder forShownAcitivityTypes() {
            parameters.includedTypes = Arrays.asList(ActivityType.TYPES_ACTIVITY_SHOWN);
            return this;
        }

        public Builder forUsers(String... userIds) {
            parameters.userIds = Arrays.asList(userIds);
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

        public Builder currentUser(String userId) {
            parameters.currentUserId = userId;
            return this;
        }

        public ActivityTimelineParameters build() {
            if (parameters.getUserIds() == null || parameters.getUserIds().isEmpty()) {
                throw new IllegalArgumentException("User list in TimelineParameters must not be null or empty");
            }
            if (parameters.currentUserId == null) {
                throw new IllegalArgumentException("Must specify the current user id");
            }
            return parameters;
        }

        private List<String> allKnownActivityTypes() {
            return Arrays.asList(ActivityType.TYPES_ACTIVITY);
        }
    }
}
