package com.shootr.mobile.domain.bus;

public interface FloatingPlayerState {

    String PLAY_PRESSED = "PLAY_PRESSED";
    String ACTIVITY_PAUSED = "ACTIVITY_PAUSED";
    String IN_APP_VIDEO = "IN_APP_VIDEO";

    interface Receiver {

        void onPlayerStateChanged(Event event);
    }

    class Event {
        String state;
        String videoId;
        String streamId;
        double currentSecond;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getStreamId() {
            return streamId;
        }

        public void setStreamId(String streamId) {
            this.streamId = streamId;
        }

        public double getCurrentSecond() {
            return currentSecond;
        }

        public void setCurrentSecond(double currentSecond) {
            this.currentSecond = currentSecond;
        }
    }
}
