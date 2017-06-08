package com.shootr.mobile.domain.bus;

public interface WatchUpdateRequest {

    interface Receiver {

        void onWatchUpdateRequest(Event event);
    }

    class Event {

        public Event(boolean localOnly) {
            this.localOnly = localOnly;
        }

        private boolean localOnly;

        public boolean isLocalOnly() {
            return localOnly;
        }

        public void setLocalOnly(boolean localOnly) {
            this.localOnly = localOnly;
        }
    }
}
