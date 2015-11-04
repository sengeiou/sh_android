package com.shootr.mobile.domain.bus;

public interface ShotSent {

    interface Receiver {

        void onShotSent(Event event);

    }

    class Event {

        private com.shootr.mobile.domain.Shot shot;

        public Event(com.shootr.mobile.domain.Shot shot) {
            this.shot = shot;
        }

        public com.shootr.mobile.domain.Shot getShot() {
            return shot;
        }

        @Override public String toString() {
            return "Event{" +
              "shot=" + shot.toString() +
              '}';
        }
    }

}
