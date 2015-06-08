package com.shootr.android.domain;

import java.util.Arrays;
import java.util.List;

public class ActivityTimelineParameters extends TimelineParameters {

    private List<String> includedTypes;
    private String excludedType;

    public List<String> getIncludedTypes() {
        return includedTypes;
    }

    public String getExcludedType() {
        return excludedType;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private ActivityTimelineParameters parameters = new ActivityTimelineParameters();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            parameters.limit = DEFAULT_LIMIT;
            parameters.sinceDate = DEFAULT_SINCE_DATE;
            parameters.excludedType = ShotType.COMMENT;
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

        public ActivityTimelineParameters build() {
            if (parameters.getUserIds() == null || parameters.getUserIds().isEmpty()) {
                throw new IllegalArgumentException("User list in TimelineParameters must not be null or empty");
            }
            return parameters;
        }

        private List<String> allKnownActivityTypes() {
            return Arrays.asList(ShotType.TYPES_ACTIVITY);
        }
    }


}
