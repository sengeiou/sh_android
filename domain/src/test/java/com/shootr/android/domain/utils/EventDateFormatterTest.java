package com.shootr.android.domain.utils;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventDateFormatterTest {

    @Mock EventDateTimeTextProvider timeTextFormatter;
    @Mock TimeUtils timeUtils;

    EventDateFormatter formatter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        formatter = new EventDateFormatter(timeTextFormatter, timeUtils);
    }

    //region Now
    @Test public void whenDateOneMinuteAgoFormatStartingNow() throws Exception {
        long now = time(12, 1);
        long eventDate = time(12, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getStartingNowFormat();
    }

    @Test public void whenDateOneMinuteFromNowFormatStartingNow() throws Exception {
        long now = time(12, 1);
        long eventDate = time(12, 2);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getStartingNowFormat();
    }

    @Test public void whenDateSameAsNowFormatStartingNow() throws Exception {
        long now = time(12, 1);
        long eventDate = now;
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getStartingNowFormat();
    }
    //endregion

    // --- Past ---

    //region Minutes ago
    @Test public void whenDate1Minute1SecondAgoFormatMinutesAgo() throws Exception {
        long now = time(12, 30);
        long eventDate = time(12, 28, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getMinutesAgoFormat(anyDate(), anyInt());
    }

    @Test public void whenDate59MinutesAgoInSameHourFormatMinutesAgo() throws Exception {
        long now = time(12, 59);
        long eventDate = time(12, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getMinutesAgoFormat(anyDate(), anyInt());
    }

    @Test public void whenDate59MinutesAgoInDifferentHourFormatMinutesAgo() throws Exception {
        long now = time(12, 0);
        long eventDate = time(11, 1);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getMinutesAgoFormat(anyDate(), anyInt());
    }
    //endregion

    //region Hours Ago
    @Test public void whenDate60MinutesAgoFormatHoursAgo() throws Exception {
        long now = time(12, 30);
        long eventDate = time(11, 30);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getHoursAgoFormat(anyDate());
    }

    @Test public void whenDate12HoursAgoFormatHoursAgo() throws Exception {
        long now = time(12, 30);
        long eventDate = time(0, 30);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getHoursAgoFormat(anyDate());
    }

    @Test public void whenDate23Hours59MinutesAgoSameDayFormatHoursAgo() throws Exception {
        long now = time(23, 59);
        long eventDate = time(0, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getHoursAgoFormat(anyDate());
    }
    //endregion

    //region Yesterday
    @Test public void whenDate12Hours1MinuteAgoDifferentDayFormatYesterday() throws Exception {
        long now = date(2, 1, 2001) + time(12, 0);
        long eventDate = date(1, 1, 2001) + time(23, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getYesterdayFormat(anyDate());
    }

    @Test public void whenDate23Hours59MinutesAgoDifferentDayFormatYesterday() throws Exception {
        long now = date(2, 1, 2001) + time(12, 0);
        long eventDate = date(1, 1, 2001) + time(12, 1);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getYesterdayFormat(anyDate());
    }

    @Test public void whenDate24Hours1MinuteAgoDayBeforeFormatYesterday() throws Exception {
        long now = date(2, 1, 2001) + time(12, 1);
        long eventDate = date(1, 1, 2001) + time(12, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getYesterdayFormat(anyDate());
    }
    //endregion

    //region Days ago
    @Test public void whenDate24Hours1MinuteAgoDayBeforeYesterdayFormatDaysAgo() throws Exception {
        long now = date(3, 1, 2001) + time(0, 0);
        long eventDate = date(1, 1, 2001) + time(23, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDaysAgoFormat(anyDate());
    }

    @Test public void whenDate7Days23Hours59MinutesAgoFormatDaysAgo() throws Exception {
        long now = date(17, 1, 2001) + time(23, 59);
        long eventDate = date(10, 1, 2001) + time(0, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDaysAgoFormat(anyDate());
    }
    //endregion

    //region Date and Time
    @Test public void whenDate8DaysAgoFormatDateTime() throws Exception {
        long now = date(18, 1, 2001);
        long eventDate = date(10, 1, 2001);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndTimeFormat(anyDate());
    }

    @Test public void whenDate10MonthsAgoFormatDateTime() throws Exception {
        long now = date(1, 10, 2001);
        long eventDate = date(1, 1, 2001);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndTimeFormat(anyDate());
    }

    @Test public void whenDate1DayBefore1YearAgoFormatDateTime() throws Exception {
        long now = date(30, 12, 2001);
        long eventDate = date(31, 12, 2000);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndTimeFormat(anyDate());
    }
    //endregion

    //region Date and Year
    @Test public void whenDate1YearAgoFormatDateYear() throws Exception {
        long now = date(31, 12, 2001);
        long eventDate = date(31, 12, 2000);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndYearFormat(anyDate());
    }

    @Test public void whenDate1Year1HourAgoFormatDateYear() throws Exception {
        long now = date(1, 1, 2001) + time(1, 0);
        long eventDate = date(1, 1, 2000) + time(0, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndYearFormat(anyDate());
    }
    //endregion

    // --- Future ---

    //region Starting in minutes
    @Test public void whenDate1Minute1SecondFromNowFormatStartingInMinutes() throws Exception {
        long now = time(12, 28, 59);
        long eventDate = time(12, 30, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getStartingInMinutesFormat(anyDate());
    }

    @Test public void whenDate59MinutesFromNowFormatStartingInMinutes() throws Exception {
        long now = time(12, 0);
        long eventDate = time(12, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getStartingInMinutesFormat(anyDate());
    }
    //endregion

    //region Starting in hours
    @Test public void whenDate60MinutesFromNowFormatStartingInHours() throws Exception {
        long now = time(11, 30, 59);
        long eventDate = time(12, 30);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getStartingInHoursFormat(anyDate());
    }

    @Test public void whenDate11Hours59MinutesFromNowSameDayFormatStartingInHours() throws Exception {
        long now = time(0, 0);
        long eventDate = time(11, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getStartingInHoursFormat(anyDate());
    }
    //endregion

    //region Today
    @Test public void whenDate12HoursFromNowSameDayFormatToday() throws Exception {
        long now = time(0, 30);
        long eventDate = time(12, 30);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getTodayFormat(anyDate());
    }

    @Test public void whenDate23Hours59MinutesFromNowSameDayFormatToday() throws Exception {
        long now = time(0, 0);
        long eventDate = time(23, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getTodayFormat(anyDate());
    }
    //endregion

    //region Tomorrow
    @Test public void whenDate12HoursFromNowDifferentDayFormatTomorrow() throws Exception {
        long now = date(1, 1, 2001) + time(12, 30);
        long eventDate = date(2, 1, 2001) + time(0, 30);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getTomorrowFormat(anyDate());
    }

    @Test public void whenDate23Hours59MinutesFromNowDifferentDayFormatTomorrow() throws Exception {
        long now = date(1, 1, 2001) + time(12, 0);
        long eventDate = date(2, 1, 2001) + time(11, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getTomorrowFormat(anyDate());
    }

    @Test public void whenDate24HoursFromNowNextDayFormatTomorrow() throws Exception {
        long now = date(1, 1, 2001) + time(12, 0);
        long eventDate = date(2, 1, 2001) + time(12, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getTomorrowFormat(anyDate());
    }

    @Test public void shouldDate47Hours59MinutesFromNowNextDayFormatTomorrow() throws Exception {
        long now = date(1, 1, 2001) + time(0, 0);
        long eventDate = date(2, 1, 2001) + time(23, 59);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getTomorrowFormat(anyDate());
    }
    //endregion

    //region Week day and Time
    @Test public void whenDate24Hours1MinuteFromNowInTwoDaysFormatWeekDayAndTime() throws Exception {
        long now = date(1, 1, 2001) + time(23, 59);
        long eventDate = date(3, 1, 2001) + time(0, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getWeekDayAndTimeFormat(anyDate());
    }

    @Test public void whenDate47Hours59MinutesFromNowInTwoDaysFormatWeekDayAndTime() throws Exception {
        long now = date(1, 1, 2001) + time(23, 59);
        long eventDate = date(3, 1, 2001) + time(0, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getWeekDayAndTimeFormat(anyDate());
    }
    //endregion

    //region Date and Time
    @Test public void whenDate48HoursFromNowFormatDateAndTime() throws Exception {
        long now = date(1, 1, 2001) + time(12, 0);
        long eventDate = date(3, 1, 2001) + time(12, 0);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndTimeFormat(anyDate());
    }

    @Test public void whenDate1DayBefore1YearFromNowFormatDateAndTime() throws Exception {
        long now = date(2, 1, 2001);
        long eventDate = date(1, 1, 2002);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndTimeFormat(anyDate());
    }
    //endregion

    //region Date and Year
    @Test public void whenDate1YearFromNowFormatDateAndYear() throws Exception {
        long now = date(1, 1, 2001);
        long eventDate = date(1, 1, 2002);
        setNow(now);

        formatter.formatEventDate(eventDate);

        verify(timeTextFormatter).getDateAndYearFormat(anyDate());
    }
    //endregion

    //region Date constructors
    private long date(int day, int month, int year) {
        MutableDateTime date = new MutableDateTime(0);
        date.setDayOfMonth(day);
        date.setMonthOfYear(month);
        date.setYear(year);
        return date.getMillis();
    }

    private long time(int hour, int minutes, int seconds) {
        MutableDateTime time = new MutableDateTime(0);
        time.setHourOfDay(hour);
        time.setMinuteOfHour(minutes);
        time.setSecondOfMinute(seconds);
        return time.getMillis();
    }

    private long time(int hour, int minutes) {
        return time(hour, minutes, 0);
    }
    //endregion

    private void setNow(long now) {
        when(timeUtils.getCurrentTime()).thenReturn(now);
    }

    private DateTime anyDate() {
        return any(DateTime.class);
    }
}
