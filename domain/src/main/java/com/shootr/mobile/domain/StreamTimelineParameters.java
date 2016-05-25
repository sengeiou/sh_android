package com.shootr.mobile.domain;

public class StreamTimelineParameters extends TimelineParameters {

    private String streamId;

    private String[] shotTypes;

    private String userId;

    private Boolean isRealTime;

    private String[] streamTypes;

    private StreamTimelineParameters() {
        /* private constructor, use builder */
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStreamId() {
        return streamId;
    }

    public String[] getShotTypes() {
        return shotTypes;
    }

    public void setShotTypes(String[] shotTypes) {
        this.shotTypes = shotTypes;
    }

    public String getUserId() {
        return userId;
    }

    public Boolean isRealTime() {
        return isRealTime;
    }

    public void setIsRealTime(Boolean isRealTime) {
        this.isRealTime = isRealTime;
    }

    public String[] getStreamTypes() {
        return streamTypes;
    }

    public void setStreamTypes(String[] streamTypes) {
        this.streamTypes = streamTypes;
    }

    public static class Builder {

        private StreamTimelineParameters parameters = new StreamTimelineParameters();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            parameters.limit = DEFAULT_LIMIT;
            parameters.sinceDate = DEFAULT_SINCE_DATE;
            parameters.shotTypes = ShotType.TYPES_SHOWN;
            parameters.streamTypes = StreamMode.TYPES_STREAM;
        }

        public Builder forStream(Stream stream) {
            parameters.streamId = stream.getId();
            return this;
        }

        public Builder forStream(String idStream) {
            parameters.streamId = idStream;
            return this;
        }

        public Builder forUser(String idUser) {
            parameters.userId = idUser;
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

        public Builder realTime(Boolean isRealTime) {
            parameters.isRealTime = isRealTime;
            return this;
        }

        public StreamTimelineParameters build() {
            return parameters;
        }
    }
}
