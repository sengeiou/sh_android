package com.shootr.mobile.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;

import com.shootr.mobile.R;
import com.shootr.mobile.domain.utils.TimeUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class AndroidTimeUtils implements TimeUtils {

    @Inject public AndroidTimeUtils() {
    }

    private long currentOffset = 0L;

    @Override public long getCurrentTime() {
        return getSystemCurrentTime()+ currentOffset;
    }

    @Override public void setCurrentTime(long timeMilliseconds) {
        currentOffset = timeMilliseconds - getSystemCurrentTime();
    }

    private long getSystemCurrentTime() {
        return System.currentTimeMillis();
    }


    public static TimeZone getDisplayTimeZone() {
        return TimeZone.getDefault();
    }


    public String getElapsedTime(Context context, long publishTime) {
        long difference =getCurrentTime() - publishTime;

        long days =  TimeUnit.MILLISECONDS.toDays(difference);
        Resources res = context.getResources();
        if(days>0){
            String time = res.getString(R.string.days);
            return String.valueOf(days+time);
        }

       long hours = TimeUnit.MILLISECONDS.toHours(difference);
       if (hours > 0) {
            String time = res.getString(R.string.hours);
            return String.valueOf(hours+time);
       }
       long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        if(minutes>0){
            String time = res.getString(R.string.minutes);
            return String.valueOf(minutes+time);
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
        if(seconds>0){
            String time = res.getString(R.string.seconds);
            return String.valueOf(seconds+time);
        }
        return res.getString(R.string.now);
    }
    /**
     * @return String short format for a date
     */
    public static String formatShortDate(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(), DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR, getDisplayTimeZone().getID()).toString();
    }

    /**
     * @return String short format date for a date time
     */
    public static String formatShortTime(Date date) {
        DateFormat mFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        TimeZone mTimeZone = getDisplayTimeZone();
        if (mTimeZone != null) {
            mFormat.setTimeZone(mTimeZone);
        }
        return mFormat.format(date);
    }


    public static long getMilisecondsByDaysNumber(Integer daysNumber){
        return TimeUnit.MILLISECONDS.convert(daysNumber, TimeUnit.DAYS);
    }

    public static Long getNDaysAgo(Integer daysNumber){
        long daysInMillis = getMilisecondsByDaysNumber(daysNumber);
        return System.currentTimeMillis() - daysInMillis;
    }

    @Override public Date getCurrentDate() {
        return new Date(getCurrentTime());
    }
}
