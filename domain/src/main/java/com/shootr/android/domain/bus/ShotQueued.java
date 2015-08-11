package com.shootr.android.domain.bus;

import com.shootr.android.domain.QueuedShot;

public interface ShotQueued {

    interface Receiver {

        void onShotQueued(Stream stream);
    }

    class Stream {

        private QueuedShot queuedShot;

        public Stream(QueuedShot queuedShot) {
            this.queuedShot = queuedShot;
        }

        public QueuedShot getQueuedShot() {
            return queuedShot;
        }

        @Override public String toString() {
            return "Stream{" +
              "queuedShot=" + queuedShot.toString() +
              '}';
        }
    }
}
