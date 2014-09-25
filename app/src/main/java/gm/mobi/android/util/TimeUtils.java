package gm.mobi.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.text.format.Time;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import gm.mobi.android.BuildConfig;
import gm.mobi.android.R;


public class TimeUtils {

    public static TimeZone getDisplayTimeZone(Context context) {
        return TimeZone.getDefault();
    }


    public static String getElapsedTime(Context context, long publishTime) {
        long difference = System.currentTimeMillis() - publishTime;

        long days =  TimeUnit.MILLISECONDS.toDays(difference);
        if(days>0){
            String time = context.getResources().getString(R.string.days);
            return String.valueOf(days+time);
        }

       long hours = TimeUnit.MILLISECONDS.toHours(difference);
       if (hours > 0) {
            String time = context.getResources().getString(R.string.hours);
            return String.valueOf(hours+time);
       }
       long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        if(minutes>0){
            String time = context.getResources().getString(R.string.minutes);
            return String.valueOf(minutes+time);
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
        if(seconds>0){
            String time = context.getResources().getString(R.string.seconds);
            return String.valueOf(seconds+time);
        }
        return "";
    }
    /**
     * @return String short format for a date
     */
    public static String formatShortDate(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(), DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR, getDisplayTimeZone(context).getID()).toString();
    }

    /**
     * @return String short format date for a date time
     */
    public static String formatShortTime(Context context, Date date) {
        DateFormat mFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        TimeZone mTimeZone = getDisplayTimeZone(context);
        if (mTimeZone != null) mFormat.setTimeZone(mTimeZone);
        return mFormat.format(date);
    }

    /*Format date to : case Today : It shows TODAY ; case Tomorrow: It shows TOMORROW; case Yesterday : It shows YESTERDAY othercase: shows SHORT FORMAT DATE*/
    public static String formatHumanFriendlyShortDate(final Context context, long timestamp) {return null;}
//        long mLocalTimeStamp, mLocalTime;
////        long mNow = getCurrentTime(context);
//        TimeZone mTimeZone = getDisplayTimeZone(context);
//        mLocalTimeStamp = timestamp + mTimeZone.getOffset(timestamp);
//        mLocalTime = mNow + mTimeZone.getOffset(mNow);
//        long dayOrd = mLocalTimeStamp / 86400000L;
//        long nowOrd = mLocalTime / 86400000L;
//        if (dayOrd == nowOrd) {
//            return context.getString(R.string.day_title_today);
//        } else if (dayOrd == nowOrd - 1) {
//            return context.getString(R.string.day_title_yesterday);
//        } else if (dayOrd == nowOrd + 1) {
//            return context.getString(R.string.day_title_tomorrow);
//        } else {
//            return formatShortDate(context, new Date(timestamp));
//        }

    public static long getMilisecondsByDaysNumber(Integer daysNumber){
        return TimeUnit.MILLISECONDS.convert(daysNumber, TimeUnit.DAYS);
    }

    public static Long getNDaysAgo(Integer daysNumber){
        long daysInMillis = getMilisecondsByDaysNumber(daysNumber);
        return System.currentTimeMillis() - daysInMillis;
    }

    /**
     * Source: Android's DateUtil
     * @param c
     * @param millis
     * @return
     */
    public static CharSequence getRelativeTimeSpanString(Context c, long millis) {
        String result;
        long now = System.currentTimeMillis();
        long span = Math.abs(now - millis);
        synchronized (TimeUtils.class) {
            if (sNowTime == null) {
                sNowTime = new Time();
            }
            if (sThenTime == null) {
                sThenTime = new Time();
            }
            sNowTime.set(now);
            sThenTime.set(millis);
            if (span < DateUtils.DAY_IN_MILLIS && sNowTime.weekDay == sThenTime.weekDay) {
                // Same day
                int flags = DateUtils.FORMAT_SHOW_TIME;
                result = DateUtils.formatDateRange(c, millis, millis, flags);
            } else if (sNowTime.year != sThenTime.year) {
                // Different years
                int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NUMERIC_DATE;
                result = DateUtils.formatDateRange(c, millis, millis, flags);
                // This is a date (like "10/31/2008" so use the date preposition)
            } else {
                // Default
                int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH;
                result = DateUtils.formatDateRange(c, millis, millis, flags);
            }
        }
        return result;
    }

    private static Time sNowTime;
    private static Time sThenTime;

}
