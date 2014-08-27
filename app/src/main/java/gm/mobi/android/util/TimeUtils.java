package gm.mobi.android.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

import gm.mobi.android.BuildConfig;
import gm.mobi.android.R;


public class TimeUtils {

    public static TimeZone getDisplayTimeZone(Context context) {
        return TimeZone.getDefault();
    }

    public static long getCurrentTime(Context context) {
        long currentTime = System.currentTimeMillis();
        if (BuildConfig.DEBUG) {
            //For testing another times
            currentTime = PrefUtils.getMockPreferences(context).getLong(PrefUtils.PREF_MOCK_CURRENT_TIME, currentTime);
        }
        return currentTime;
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
    public static String formatHumanFriendlyShortDate(final Context context, long timestamp) {
        long mLocalTimeStamp, mLocalTime;
        long mNow = getCurrentTime(context);
        TimeZone mTimeZone = getDisplayTimeZone(context);
        mLocalTimeStamp = timestamp + mTimeZone.getOffset(timestamp);
        mLocalTime = mNow + mTimeZone.getOffset(mNow);
        long dayOrd = mLocalTimeStamp / 86400000L;
        long nowOrd = mLocalTime / 86400000L;
        if (dayOrd == nowOrd) {
            return context.getString(R.string.day_title_today);
        } else if (dayOrd == nowOrd - 1) {
            return context.getString(R.string.day_title_yesterday);
        } else if (dayOrd == nowOrd + 1) {
            return context.getString(R.string.day_title_tomorrow);
        } else {
            return formatShortDate(context, new Date(timestamp));
        }
    }


}
