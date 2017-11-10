package com.shootr.mobile.domain.model.activity;

import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.TimelineParameters;
import java.util.Arrays;
import java.util.List;

public class ActivityTimelineParameters extends TimelineParameters {

    public static final String FOLLOWING = "HOME";
    public static final String ME = "HISTORIC";
    private List<String> includedTypes;
    private String[] streamTypes;
    private String[] shotTypes;
    private String activityFilter;
    private Boolean isMeTimeline;

    public static Builder builder() {
        return new Builder();
    }

    public List<String> getIncludedTypes() {
        return includedTypes;
    }

    public void excludeHiddenTypes() {
        this.includedTypes = Arrays.asList(ActivityType.TYPES_ACTIVITY_SHOWN);
    }

    public String[] getStreamTypes() {
        return streamTypes;
    }

    public String[] getShotTypes() {
        return shotTypes;
    }

    public String getActivityFilter() {
        return activityFilter;
    }

    public Boolean isMeTimeline() {
        return isMeTimeline;
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
            parameters.streamTypes = allKnownStreamTypes();
            parameters.shotTypes = allKnownShotTypes();
            parameters.isMeTimeline = false;
            parameters.activityFilter = FOLLOWING;
        }

        public Builder since(Long sinceDate) {
            parameters.sinceDate = sinceDate;
            return this;
        }

        public Builder maxDate(Long maxDate) {
            parameters.maxDate = maxDate;
            return this;
        }

        public Builder isMeTimeline(Boolean isMe) {
            parameters.isMeTimeline = isMe;
            if (isMe) {
                parameters.activityFilter = ME;
            } else {
                parameters.activityFilter = FOLLOWING;
            }
            return this;
        }

        public ActivityTimelineParameters build() {
            return parameters;
        }

        private List<String> allKnownActivityTypes() {
            return Arrays.asList(ActivityType.TYPES_ACTIVITY);
        }

        private String[] allKnownStreamTypes() {
            return StreamMode.TYPES_STREAM;
        }

        private String[] allKnownShotTypes() {
            return ShotType.TYPES_SHOWN;
        }
    }
}
