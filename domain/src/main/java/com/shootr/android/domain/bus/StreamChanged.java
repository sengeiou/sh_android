package com.shootr.android.domain.bus;

public interface StreamChanged {

    interface Receiver {

        void onStreamChanged(Stream stream);
    }

    class Stream {

        private String newStreamId;

        public Stream(String newStreamId) {
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
