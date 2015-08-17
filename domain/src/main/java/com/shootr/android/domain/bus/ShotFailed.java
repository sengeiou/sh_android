package com.shootr.android.domain.bus;

import com.shootr.android.domain.Shot;

public interface ShotFailed {

    interface Receiver {

        void onShotFailed(Event event);

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
