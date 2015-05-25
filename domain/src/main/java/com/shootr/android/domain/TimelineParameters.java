package com.shootr.android.domain;

import java.util.List;

public abstract class TimelineParameters {

    public static final Long DEFAULT_SINCE_DATE = 0L;
    protected static final Integer DEFAULT_LIMIT = 100;

    protected List<String> userIds;
    protected Long sinceDate;
    protected Long maxDate;
    protected Integer limit;

    public List<String> getUserIds() {
        return userIds;
    }

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
