package com.shootr.mobile.domain.bus;

import com.shootr.mobile.domain.model.shot.BaseMessage;

public interface ShotSent {

    interface Receiver {

        void onShotSent(Event event);
    }

    class Event {

        private BaseMessage shot;

        public Event(BaseMessage shot) {
            this.shot = shot;
        }

        public BaseMessage getShot() {
            return shot;
        }

        @Override public String toString() {
            return "Event{" +
              "shot=" + shot.toString() +
              '}';
        }
    }
}
