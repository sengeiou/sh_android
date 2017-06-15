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

  private final long MILISECONDS_ON_A_DAY = 86400000;
  private final long MILISECONDS_ON_A_HOUR = 3600000;
  private final long MILISECONDS_ON_A_MINUTE = 60000;

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

  private String getDays(long difference, Resources res) {
    long days = TimeUnit.MILLISECONDS.toDays(difference);
    if (days > 0) {
      difference = difference - (MILISECONDS_ON_A_DAY * days);
      long hours = TimeUnit.MILLISECONDS.toHours(difference);
      String timeDays = res.getQuantityString(R.plurals.days_left, (int) days, (int) days);
      String timeHours =
          ((hours > 0) ? " " + res.getQuantityString(R.plurals.hours_left, (int) hours, (int) hours)
              : "");
      return res.getString(R.string.left, timeDays + timeHours);
    } else {
      return null;
    }
  }

  private String getHours(long difference, Resources res) {
    long hours = TimeUnit.MILLISECONDS.toHours(difference);
    if (hours > 0) {
      difference = difference - (MILISECONDS_ON_A_HOUR * hours);
      long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
      String timeHours = res.getQuantityString(R.plurals.hours_left, (int) hours, (int) hours);
      String timeMinutes =
          ((minutes > 0) ? " " + res.getQuantityString(R.plurals.minutes_left, (int) minutes,
              (int) minutes) : "");
      return res.getString(R.string.left, timeHours + timeMinutes);
    } else {
      return null;
    }
  }

  private String getMinutes(long difference, Resources res) {
    long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
    if (minutes > 0) {
      difference = difference - (MILISECONDS_ON_A_MINUTE * minutes);
      long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
      String timeMinutes =
          res.getQuantityString(R.plurals.minutes_left, (int) minutes, (int) minutes);
      String timeSeconds =
          ((seconds > 0) ? " " + res.getQuantityString(R.plurals.seconds_left, (int) seconds,
              (int) seconds) : "");
      return res.getString(R.string.left, timeMinutes + timeSeconds);
    } else {
      return null;
    }
  }

  private String getSeconds(long difference, Resources res) {
    long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
    if (seconds > 0) {
      String timeSeconds =
          res.getQuantityString(R.plurals.seconds_left, (int) seconds, (int) seconds);
      return res.getString(R.string.left, timeSeconds);
    } else {
      return null;
    }
  }

  public String getPollElapsedTime(Context context, Long timeToFinish) {

    Resources res = context.getResources();
    if (timeToFinish != -1) {
      long difference = timeToFinish - getCurrentTime();

      String daysResulted = getDays(difference, res);
      if (daysResulted != null) {
        return daysResulted;
      }
      String hoursResulted = getHours(difference, res);
      if (hoursResulted != null) {
        return hoursResulted;
      }
      String minutesResulted = getMinutes(difference, res);
      if (minutesResulted != null) {
        return minutesResulted;
      }
      String secondsResulted = getSeconds(difference, res);
      if (secondsResulted != null) {
        return secondsResulted;
      }
    }
    return res.getString(R.string.poll_manual_close);
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
