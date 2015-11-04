package com.shootr.mobile.domain.bus;

public interface StreamChanged {

    interface Receiver {

        void onStreamChanged(Event event);
    }

    class Event {

        private String newStreamId;

        public Event(String newStreamId) {
            this.newStreamId = newStreamId;
        }

        public String getNewStreamId() {
            return newStreamId;
        }

        @Override public String toString() {
            return "Stream{" +
              "newStreamId=" + newStreamId +
              '}';
        }
    }
}
