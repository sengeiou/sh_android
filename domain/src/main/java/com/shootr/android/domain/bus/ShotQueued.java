package com.shootr.android.domain.bus;

import com.shootr.android.domain.QueuedShot;

public interface ShotQueued {

    interface Receiver {

        void onShotQueued(Event event);
    }

    class Event {

        private QueuedShot queuedShot;

        public Event(QueuedShot queuedShot) {
            this.queuedShot = queuedShot;
        }

        public QueuedShot getQueuedShot() {
            return queuedShot;
        }

        @Override public String toString() {
            return "Event{" +
              "queuedShot=" + queuedShot.toString() +
              '}';
        }
    }
}
