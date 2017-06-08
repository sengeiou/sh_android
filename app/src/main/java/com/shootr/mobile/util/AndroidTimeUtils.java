package com.shootr.mobile.util;

import android.content.Context;
import android.content.res.Resources;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.utils.TimeUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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

    public String getPollElapsedTime(Context context, long publishTime) {
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
        return res.getString(R.string.closed);
    }

    public String getHourWithDate(long publishTime) {
        long difference = getCurrentTime() - publishTime;
        long days = TimeUnit.MILLISECONDS.toDays(difference);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(publishTime);

        if (days > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            return sdf.format(calendar.getTime());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(calendar.getTime());
        }
    }

    @Override public Date getCurrentDate() {
        return new Date(getCurrentTime());
    }
}
