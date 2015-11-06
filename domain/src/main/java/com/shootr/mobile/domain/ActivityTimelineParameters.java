package com.shootr.mobile.domain;

import java.util.Arrays;
import java.util.List;

public class ActivityTimelineParameters extends TimelineParameters {

    private List<String> includedTypes;

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getIncludedTypes() {
        return includedTypes;
    }

    public void excludeHiddenTypes() {
        this.includedTypes = Arrays.asList(ActivityType.TYPES_ACTIVITY_SHOWN);
    }

    public static class Builder {

        private ActivityTimelineParameters parameters = new ActivityTimelineParameters();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            parameters.limit = DEFAULT_LIMIT;
            parameters.sinceDate = DEFAULT_SINCE_DATE;
            parameters.includedTypes = allKnownActivityTypes();
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
            return parameters;
        }

        private List<String> allKnownActivityTypes() {
            return Arrays.asList(ActivityType.TYPES_ACTIVITY);
        }
    }
}
