package com.shootr.mobile.domain;

public class StreamTimelineParameters extends TimelineParameters {

    private String streamId;

    private String shotType;

    private String userId;

    private StreamTimelineParameters() {
        /* private constructor, use builder */
    }

    public String getStreamId() {
        return streamId;
    }

    public String getShotType() {
        return shotType;
    }

    public String getUserId() {
        return userId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private StreamTimelineParameters parameters = new StreamTimelineParameters();

        public Builder() {
            setDefaults();
        }

        private void setDefaults() {
            parameters.limit = DEFAULT_LIMIT;
            parameters.sinceDate = DEFAULT_SINCE_DATE;
            parameters.shotType = com.shootr.mobile.domain.ShotType.COMMENT;
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

        public StreamTimelineParameters build() {
            return parameters;
        }
    }
}
