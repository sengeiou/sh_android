package com.shootr.mobile.domain.bus;

import com.shootr.mobile.domain.model.SocketMessage;

public interface EventReceived {

    interface Receiver {

        void onEvent(Event event);
    }

    class Event {

        private SocketMessage message;

        public Event(SocketMessage message) {
            this.message = message;
        }

        public SocketMessage getMessage() {
            return message;
        }
    }
}
