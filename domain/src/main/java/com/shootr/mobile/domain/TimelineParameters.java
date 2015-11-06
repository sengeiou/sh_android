package com.shootr.mobile.domain;

public abstract class TimelineParameters {

    public static final Long DEFAULT_SINCE_DATE = 0L;
    protected static final Integer DEFAULT_LIMIT = 100;

    protected Long sinceDate;
    protected Long maxDate;
    protected Integer limit;

    public Long getSinceDate() {
        return sinceDate;
    }

    public Long getMaxDate() {
        return maxDate;
    }

    public Integer getLimit() {
        return limit;
    }
}
