package gm.mobi.android.util;

import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeFormatter {

    DateTimeFormatter hourFormater = DateTimeFormat.forPattern("HH:mm");
    DateTimeFormatter dayFormater = DateTimeFormat.forPattern("MM/dd");
    DateTimeFormatter weekDayFormater = DateTimeFormat.forPattern("EEEEEEEEEEEE");

    public TimeFormatter() {

    }

    public String getDateAndTimeText(long targetTimestamp) {
        DateTime targetDate = new DateTime(targetTimestamp);
        if (isToday(targetDate)) {
            return getTodayFormat(targetDate);
        }else if (isTomorrow(targetDate)) {
            return getTomorrowFormat(targetDate);
        }else if (isLessThanAWeek(targetDate)) {
            return getDayOfWeek(targetDate);
        } else {
            return getDayFormat(targetDate);
        }
    }

    private String getDayOfWeek(DateTime targetDate) {
        return weekDayFormater.print(targetDate);
    }

    private String getDayFormat(DateTime targetDate) {
        return dayFormater.print(targetDate);
    }

    private String getTomorrowFormat(DateTime targetDate) {
        return "Tomorrow";
    }

    private boolean isToday(DateTime targetDate) {
        return getDaysUntil(targetDate) == 0;
    }

    private boolean isTomorrow(DateTime targetDate) {
        return getDaysUntil(targetDate) == 1;
    }

    private boolean isLessThanAWeek(DateTime targetDate) {
        return getDaysUntil(targetDate) < 7;
    }

    private String getTodayFormat(DateTime dateTime) {
        return hourFormater.print(dateTime);
    }


    private int getDaysUntil(DateTime targetDate) {
        return Days.daysBetween(DateTime.now(), targetDate).getDays();
    }

}
