package com.shootr.android.domain.utils;

import java.util.Date;

public interface TimeUtils {

    long getCurrentTime();

    void setCurrentTime(long timeMilliseconds);

    Date getCurrentDate();
}
