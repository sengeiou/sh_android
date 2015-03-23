package com.shootr.android.domain.bus;

public interface EventChanged {

    interface Receiver {

        void onEventChanged(Event event);
    }

    class Event {

        private Long newEventId;

        public Event(Long newEventId) {
            this.newEventId = newEventId;
        }

        public Long getNewEventId() {
            return newEventId;
        }

        @Override public String toString() {
            return "Event{" +
              "newEventId=" + newEventId +
              '}';
        }
    }
}
