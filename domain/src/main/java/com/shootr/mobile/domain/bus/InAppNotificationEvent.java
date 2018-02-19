package com.shootr.mobile.domain.bus;

import com.shootr.mobile.domain.model.InAppNotification;

public interface InAppNotificationEvent {

    interface Receiver {

        void onNotification(Event event);
    }

    class Event {
        private InAppNotification inAppNotification;

        public Event(InAppNotification inAppNotification) {
            this.inAppNotification = inAppNotification;
        }

        public InAppNotification getInAppNotification() {
            return inAppNotification;
        }

        public void setInAppNotification(InAppNotification inAppNotification) {
            this.inAppNotification = inAppNotification;
        }

        @Override public String toString() {
            return "Event{" + "inAppNotification=" + inAppNotification + '}';
        }
    }
}
