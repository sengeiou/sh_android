package com.shootr.mobile.domain.model.stream;

public abstract class TimelineParameters {

    public static final Long DEFAULT_SINCE_DATE = 0L;
    protected static final Integer DEFAULT_LIMIT = 50;

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
