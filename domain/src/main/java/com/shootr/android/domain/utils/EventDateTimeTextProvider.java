package com.shootr.android.domain.utils;

import org.joda.time.DateTime;

public interface EventDateTimeTextProvider {

    String getStartingInMinutesFormat(DateTime date);

    String getMinutesAgoFormat(DateTime date, int numberOfMinutes);

    String getStartingNowFormat();

    String getStartingInHoursFormat(DateTime date);

    String getYesterdayFormat(DateTime date);

    String getDaysAgoFormat(DateTime date);

    String getHoursAgoFormat(DateTime date);

    String getTodayFormat(DateTime date);

    String getTomorrowFormat(DateTime date);

    String getWeekDayAndTimeFormat(DateTime date);

    String getDateAndYearFormat(DateTime date);

    String getDateAndTimeFormat(DateTime date);
}
