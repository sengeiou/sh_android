package com.shootr.android.domain;

public class EventTimelineParameters extends TimelineParameters {

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

    public String getShotType() {
        return shotType;
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

        public Builder forEvent(Stream stream) {
            parameters.eventId = stream.getId();
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
            return parameters;
        }
    }
}
