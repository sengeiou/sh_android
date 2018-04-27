package com.shootr.mobile.data.entity;

public class PeriodEntity {

  private long duration;

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PeriodEntity that = (PeriodEntity) o;

    return getDuration() == that.getDuration();
  }

  @Override public int hashCode() {
    return (int) (getDuration() ^ (getDuration() >>> 32));
  }
}
