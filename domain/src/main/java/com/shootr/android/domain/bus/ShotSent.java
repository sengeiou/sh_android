package com.shootr.android.domain.bus;

import com.shootr.android.domain.Shot;

public interface ShotSent {

    interface Receiver {

        void onShotSent(Stream stream);

    }

    class Stream {

        private Shot shot;

        public Stream(Shot shot) {
            this.shot = shot;
        }

        public Shot getShot() {
            return shot;
        }

        @Override public String toString() {
            return "Stream{" +
              "shot=" + shot.toString() +
              '}';
        }
    }

}
