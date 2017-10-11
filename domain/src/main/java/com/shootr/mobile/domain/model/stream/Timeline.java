package com.shootr.mobile.domain.model.stream;

import com.shootr.mobile.domain.model.shot.Shot;
import java.util.List;

public class Timeline {

    private List<Shot> shots;
    private boolean isFirstCall;

    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timeline)) return false;

        Timeline timeline = (Timeline) o;

        return !(shots != null ? !shots.equals(timeline.shots) : timeline.shots != null);
    }

    @Override public int hashCode() {
        return shots != null ? shots.hashCode() : 0;
    }

    @Override public String toString() {
        return "Timeline{" +
          "shots=" + shots +
          '}';
    }

    public boolean isFirstCall() {
        return isFirstCall;
    }

    public void setFirstCall(boolean firstCall) {
        isFirstCall = firstCall;
    }
}
