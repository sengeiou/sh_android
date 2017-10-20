package com.shootr.mobile.data.prefs;

public class ShowcaseStatus {

  private boolean shouldShowShowcase;
  private int timesViewed;
  public final int NUM_TIMES_VISIBLE = 1;

  ShowcaseStatus(boolean shouldShowShowcase) {
    this.shouldShowShowcase = shouldShowShowcase;
    timesViewed = 0;
  }

  public boolean shouldShowShowcase() {
    return shouldShowShowcase;
  }

  public void setShouldShowShowcase(boolean shouldShowShowcase) {
    this.shouldShowShowcase = shouldShowShowcase;
  }

  public int getTimesViewed() {
    return timesViewed;
  }

  public void setTimesViewed(int timesViewed) {
    this.timesViewed = timesViewed;
    if (this.timesViewed >= NUM_TIMES_VISIBLE) {
      this.shouldShowShowcase = false;
    }
  }

  @Override public String toString() {
    return "ShowcaseStatus{" +
        "shouldShowShowcase=" + shouldShowShowcase +
        ", timesViewed=" + timesViewed +
        '}';
  }
}
