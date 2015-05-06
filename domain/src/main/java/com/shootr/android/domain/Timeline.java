package com.shootr.android.domain;

import java.util.List;

public class Timeline {

    private List<Shot> shots;
    private TimelineParameters parameters;

    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
    }

    public void setParameters(TimelineParameters parameters) {
        this.parameters = parameters;
    }

    public TimelineParameters getParameters() {
        return parameters;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timeline timeline = (Timeline) o;

        if (!shots.equals(timeline.shots)) return false;
        return !(parameters != null ? !parameters.equals(timeline.parameters) : timeline.parameters != null);

    }

    @Override public int hashCode() {
        int result = shots.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Timeline{" +
                "shots=" + shots +
                ", parameters=" + parameters +
                '}';
    }
}
