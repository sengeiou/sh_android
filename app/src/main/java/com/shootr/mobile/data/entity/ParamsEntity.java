package com.shootr.mobile.data.entity;

public class ParamsEntity {

  private PeriodEntity period;
  private long lowerBound;

  public PeriodEntity getPeriod() {
    return period;
  }

  public void setPeriod(PeriodEntity period) {
    this.period = period;
  }

  public long getLowerBound() {
    return lowerBound;
  }

  public void setLowerBound(long lowerBound) {
    this.lowerBound = lowerBound;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ParamsEntity that = (ParamsEntity) o;

    if (getLowerBound() != that.getLowerBound()) return false;
    return getPeriod().equals(that.getPeriod());
  }

  @Override public int hashCode() {
    int result = getPeriod().hashCode();
    result = 31 * result + (int) (getLowerBound() ^ (getLowerBound() >>> 32));
    return result;
  }

  @Override public String toString() {
    return "ParamsEntity{" + "period=" + period + ", lowerBound=" + lowerBound + '}';
  }
}
