package com.shootr.android.data.bus;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public interface ServerDown {

    interface Receiver {

        void onServerDown(Event event);
    }

    class Event {

        private String title;
        private String message;

        public Event(String title, String message) {
            this.title = checkNotNull(title);
            this.message = checkNotNull(message);
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }
}
