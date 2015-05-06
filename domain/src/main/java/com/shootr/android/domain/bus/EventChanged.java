package com.shootr.android.domain.bus;

public interface EventChanged {

    interface Receiver {

        void onEventChanged(Event event);
    }

    class Event {

        private String newEventId;

        public Event(String newEventId) {
            this.newEventId = newEventId;
        }

        public String getNewEventId() {
            return newEventId;
        }

        @Override public String toString() {
            return "Event{" +
              "newEventId=" + newEventId +
              '}';
        }
    }
}
