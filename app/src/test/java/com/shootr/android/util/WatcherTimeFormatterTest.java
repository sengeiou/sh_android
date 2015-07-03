package com.shootr.android.util;

import com.shootr.android.domain.utils.TimeUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WatcherTimeFormatterTest {

    public static final long PRESENT_DATE_MILLIS = 1431009362000L;
    public static final long FUTURE_DATE_MILLIS = 1462631762000L;
    public static final long SOME_SECONDS_AFTER_PRESENT_DATE_MILLIS = 1431009392000L;
    public static final long TEN_MINUTES_BEFORE_PRESENT_MILLIS = 1431008192000L;
    public static final long TWO_HOURS_BEFORE_PRESENT_MILLIS = 1430997392000L;
    public static final long TWO_DAYS_BEFORE_MILLIS = 1430824592000L;
    private WatchersTimeFormatter watchersTimeFormatter;

    @Mock WatchersTimeTextFormatter watchersTimeTextFormatter;
    @Mock TimeUtils timeUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        watchersTimeFormatter = new WatchersTimeFormatter(timeUtils, watchersTimeTextFormatter);
    }

    @Test
    public void shouldShowJustEnteredFormatIfDateIsFuture(){
        when(timeUtils.getCurrentTime()).thenReturn(PRESENT_DATE_MILLIS);
        watchersTimeFormatter.jointDateText(FUTURE_DATE_MILLIS);
        verify(watchersTimeTextFormatter).getJustEnteredFormat();
    }

    @Test
    public void shouldShowJustEnteredFormatIfUserHasJustEntered(){
        when(timeUtils.getCurrentTime()).thenReturn(PRESENT_DATE_MILLIS);
        watchersTimeFormatter.jointDateText(PRESENT_DATE_MILLIS);
        verify(watchersTimeTextFormatter).getJustEnteredFormat();
    }

    @Test
    public void shouldShowLongTimeAgoFormatIfDateHasPastMoreThanAWeek(){
        when(timeUtils.getCurrentTime()).thenReturn(FUTURE_DATE_MILLIS);
        watchersTimeFormatter.jointDateText(PRESENT_DATE_MILLIS);
        verify(watchersTimeTextFormatter).getLongTimeAgoFormat();
    }

    @Test
    public void shouldShowjustEnteredFormatIfDateHasPastLessThanAMinute(){
        when(timeUtils.getCurrentTime()).thenReturn(PRESENT_DATE_MILLIS);
        watchersTimeFormatter.jointDateText(SOME_SECONDS_AFTER_PRESENT_DATE_MILLIS);
        verify(watchersTimeTextFormatter).getJustEnteredFormat();
    }

    @Test
    public void shouldShowMinutesAgoFormatIfDateHasPastLessThanAnHour(){
        when(timeUtils.getCurrentTime()).thenReturn(PRESENT_DATE_MILLIS);
        watchersTimeFormatter.jointDateText(TEN_MINUTES_BEFORE_PRESENT_MILLIS);
        verify(watchersTimeTextFormatter).getMinutesAgoFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowHoursAgoFormatIfDateHasPastLessThanADay(){
        when(timeUtils.getCurrentTime()).thenReturn(PRESENT_DATE_MILLIS);
        watchersTimeFormatter.jointDateText(TWO_HOURS_BEFORE_PRESENT_MILLIS);
        verify(watchersTimeTextFormatter).getHoursAgoFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowDaysAgoFormatIfDateHasPastLessThanAWeek(){
        DateTime presentDate = new DateTime(PRESENT_DATE_MILLIS);
        DateTime pastDate = new DateTime(TWO_DAYS_BEFORE_MILLIS);
        when(timeUtils.getCurrentTime()).thenReturn(PRESENT_DATE_MILLIS);
        watchersTimeFormatter.jointDateText(TWO_DAYS_BEFORE_MILLIS);
        verify(watchersTimeTextFormatter).getDaysAgoFormat(any(DateTime.class));
    }

    @Test public void shouldShowJustEnteredFormatWhenDateIs59Seconds() throws Exception {
        long now = date(7, 5, 2015) + time(12, 0, 59);
        long eventDate = date(7, 5, 2015) + time(12, 0, 0);
        when(timeUtils.getCurrentTime()).thenReturn(now);

        watchersTimeFormatter.jointDateText(eventDate);

        verify(watchersTimeTextFormatter).getJustEnteredFormat();
    }

    @Test public void shouldShowMinutesAgoFormatWhenDateIs1Minute() throws Exception {
        long now = date(7, 5, 2015) + time(12, 1, 0);
        long eventDate = date(7, 5, 2015) + time(12, 0, 0);
        when(timeUtils.getCurrentTime()).thenReturn(now);

        watchersTimeFormatter.jointDateText(eventDate);

        verify(watchersTimeTextFormatter).getMinutesAgoFormat(any(DateTime.class));
    }

    @Test public void shouldShowHoursAgoFormatWhenDateIs1Hours() throws Exception {
        long now = date(7, 5, 2015) + time(12, 40, 0);
        long eventDate = date(7, 5, 2015) + time(11, 40, 0);
        when(timeUtils.getCurrentTime()).thenReturn(now);

        watchersTimeFormatter.jointDateText(eventDate);

        verify(watchersTimeTextFormatter).getHoursAgoFormat(any(DateTime.class));
    }

    @Test public void shouldShowDaysAgoFormatWhenDateIs1Day() throws Exception {
        long now = date(7, 5, 2015) + time(12, 40, 0);
        long eventDate = date(6, 5, 2015) + time(12, 40, 0);
        when(timeUtils.getCurrentTime()).thenReturn(now);

        watchersTimeFormatter.jointDateText(eventDate);

        verify(watchersTimeTextFormatter).getDaysAgoFormat(any(DateTime.class));
    }

    @Test public void shouldNotShowDaysAgoFormatWhenDateIsLessThan1Day() throws Exception {
        long now = date(7, 5, 2015) + time(12, 40, 0);
        long eventDate = date(6, 5, 2015) + time(12, 41, 0);
        when(timeUtils.getCurrentTime()).thenReturn(now);

        watchersTimeFormatter.jointDateText(eventDate);

        verify(watchersTimeTextFormatter,never()).getDaysAgoFormat(any(DateTime.class));
    }

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
    //endregion

}
