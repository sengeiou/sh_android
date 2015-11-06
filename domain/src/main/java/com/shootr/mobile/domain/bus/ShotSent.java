package com.shootr.mobile.domain.bus;

import com.shootr.mobile.domain.Shot;

public interface ShotSent {

    interface Receiver {

        void onShotSent(Event event);
    }

    class Event {

        private Shot shot;

        public Event(Shot shot) {
            this.shot = shot;
        }

        public Shot getShot() {
            return shot;
        }

        @Override public String toString() {
            return "Event{" +
              "shot=" + shot.toString() +
              '}';
        }
    }
}
