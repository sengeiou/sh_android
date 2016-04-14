package com.shootr.mobile.util;

import android.content.Context;
import android.content.res.Resources;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.utils.TimeUtils;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class AndroidTimeUtils implements TimeUtils {

    @Inject public AndroidTimeUtils() {
    }

    @Override public long getCurrentTime() {
        long currentOffset = 0L;
        return getSystemCurrentTime() + currentOffset;
    }

    private long getSystemCurrentTime() {
        return System.currentTimeMillis();
    }

    public String getElapsedTime(Context context, long publishTime) {
        long difference = getCurrentTime() - publishTime;

        long days = TimeUnit.MILLISECONDS.toDays(difference);
        Resources res = context.getResources();
        if (days > 0) {
            String time = res.getString(R.string.days);
            return String.valueOf(days + time);
        }

        long hours = TimeUnit.MILLISECONDS.toHours(difference);
        if (hours > 0) {
            String time = res.getString(R.string.hours);
            return String.valueOf(hours + time);
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        if (minutes > 0) {
            String time = res.getString(R.string.minutes);
            return String.valueOf(minutes + time);
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
        if (seconds > 0) {
            String time = res.getString(R.string.seconds);
            return String.valueOf(seconds + time);
        }
        return res.getString(R.string.now);
    }

    @Override public Date getCurrentDate() {
        return new Date(getCurrentTime());
    }
}
