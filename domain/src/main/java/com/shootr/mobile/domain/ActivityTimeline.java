package com.shootr.mobile.domain;

import java.util.List;

public class ActivityTimeline {

    private List<Activity> activities;

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityTimeline)) return false;

        ActivityTimeline that = (ActivityTimeline) o;

        return !(activities != null ? !activities.equals(that.activities) : that.activities != null);
    }

    @Override public int hashCode() {
        return activities != null ? activities.hashCode() : 0;
    }

    @Override public String toString() {
        return "ActivityTimeline{" +
          "activities=" + activities +
          '}';
    }
}
