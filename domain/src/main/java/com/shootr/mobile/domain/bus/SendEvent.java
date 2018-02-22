package com.shootr.mobile.domain.bus;

import com.shootr.mobile.domain.messages.Message;

public interface SendEvent {

    interface Receiver {

        void onEvent(Event event);
    }

    class Event {

        private Message message;

        public Event(Message message) {
            this.message = message;
        }

        public Message getMessage() {
            return message;
        }

        @Override public String toString() {
            return "Event{" + "message=" + message + '}';
        }
    }
}
