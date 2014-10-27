package gm.mobi.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import gm.mobi.android.R;
import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class TimeUtils {

    public static TimeZone getDisplayTimeZone(Context context) {
        return TimeZone.getDefault();
    }


    public static String getElapsedTime(Context context, long publishTime) {
        long difference = System.currentTimeMillis() - publishTime;

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

}
