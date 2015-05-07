package com.shootr.android.utils;

import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.util.EventTimeFormatter;
import com.shootr.android.util.TimeTextFormatter;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventTimeFormatterTest {

    public static final long EIGHT_HOURS_MILLISECONDS = 1430978400000L;
    public static final long TWENTY_TWO_HOURS_MILLISECONDS = 1431028800000L;
    public static final long NEXT_DAY_ELEVEN_HOURS_MILLISECONDS = 1431075600000L;
    public static final long TWO_DAYS_LATER_MILLISECONDS = 1431133200000L;
    public static final long SOME_TIME_AGO_THIS_YEAR = 1420423200000L;
    public static final long DATE_ON_NEXT_YEAR = 1462755600000L;
    public static final long PAST_YEAR_MILLISECONDS = 1399597200L;
    public static final int RANDOM_DAYS_OF_DIFFERENCE = 30;
    @Mock TimeTextFormatter timeTextFormatter;
    @Mock TimeUtils timeUtils;

    EventTimeFormatter eventTimeFormatter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eventTimeFormatter = new EventTimeFormatter(timeTextFormatter, timeUtils);
    }

    @Test
    public void shouldShowStartingNowIfDateIsInLessThanAMinute() {
        Long timestamp = setUpDateStartingLessThanAMinuteInTimestamp();
        when(timeUtils.getCurrentTime()).thenReturn(new Date().getTime());
        eventTimeFormatter.formatEventDate(timestamp);
        verify(timeTextFormatter).getStartingNowFormat();
    }

    @Test
    public void shouldShowStartingNowIfDateIsZeroMinutes() {
        Long timestamp = setUpDateStartingLessThanAMinuteInTimestamp();
        when(timeUtils.getCurrentTime()).thenReturn(timestamp);
        eventTimeFormatter.formatEventDate(timestamp);
        verify(timeTextFormatter).getStartingNowFormat();
    }

    @Test
    public void shouldShowStartingNowIfDateIsWasInLessThanAMinute() {
        Long timestamp = setUpDateStartedLessThanAMinuteInTimestamp();
        when(timeUtils.getCurrentTime()).thenReturn(new Date().getTime());
        eventTimeFormatter.formatEventDate(timestamp);
        verify(timeTextFormatter).getStartingNowFormat();
    }

    @Test
    public void shouldShowStartingInSomeMinutesifDateIsLessThanAnHour() {
        Long timestamp = setUpDateStartingLessThanAnHourInTimestamp();
        when(timeUtils.getCurrentTime()).thenReturn(new Date().getTime());
        eventTimeFormatter.formatEventDate(timestamp);
        verify(timeTextFormatter).getStartingInMinutesFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowStartedSomeMinutesAgoifDateWasLessThanAnHour() {
        Long timestamp = setUpDateStartedLessThanAnHourInTimestamp();
        when(timeUtils.getCurrentTime()).thenReturn(new Date().getTime());
        eventTimeFormatter.formatEventDate(timestamp);
        verify(timeTextFormatter).getMinutesAgoFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowStartingInSomeHoursifDateIsLessThanTwelveHoursAndGreaterThanAnHour() {
        Long timestamp = setUpDateStartingLessThanTwelveHoursAndGreaterThanAnHourInTimestamp();
        when(timeUtils.getCurrentTime()).thenReturn(new Date().getTime());
        eventTimeFormatter.formatEventDate(timestamp);
        verify(timeTextFormatter).getStartingInHoursFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowStartedSomeHoursifDateIsLessThanTwelveHoursAndGreaterThanAnHour() {
        Long timestamp = setUpDateStaredLessThanTwelveHoursAndGreaterThanAnHourInTimestamp();
        when(timeUtils.getCurrentTime()).thenReturn(new Date().getTime());
        eventTimeFormatter.formatEventDate(timestamp);
        verify(timeTextFormatter).getHoursAgoFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowTodayFormatIfDateBetween12And24HoursAndItsToday() {
        when(timeUtils.getCurrentTime()).thenReturn(EIGHT_HOURS_MILLISECONDS);
        eventTimeFormatter.formatEventDate(TWENTY_TWO_HOURS_MILLISECONDS);
        verify(timeTextFormatter).getTodayFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowHoursAgoFormatIfDateBetween12And24HoursAndItsToday() {
        when(timeUtils.getCurrentTime()).thenReturn(TWENTY_TWO_HOURS_MILLISECONDS);
        eventTimeFormatter.formatEventDate(EIGHT_HOURS_MILLISECONDS);
        verify(timeTextFormatter).getHoursAgoFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowTomorowFormatIfDateBetween12And24HoursAndItsTomorow() {
        when(timeUtils.getCurrentTime()).thenReturn(TWENTY_TWO_HOURS_MILLISECONDS);
        when(timeTextFormatter.calculateDaysOfDifferenceBetweenDates(
                new DateTime(TWENTY_TWO_HOURS_MILLISECONDS),
                new DateTime(NEXT_DAY_ELEVEN_HOURS_MILLISECONDS))).thenReturn(1);
        eventTimeFormatter.formatEventDate(NEXT_DAY_ELEVEN_HOURS_MILLISECONDS);
        verify(timeTextFormatter).getTomorrowFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowYesterdayFormatIfDateBetween12And24HoursAndItsYesterday() {
        when(timeUtils.getCurrentTime()).thenReturn(NEXT_DAY_ELEVEN_HOURS_MILLISECONDS);
        when(timeTextFormatter.calculateDaysOfDifferenceBetweenDates(
                new DateTime(NEXT_DAY_ELEVEN_HOURS_MILLISECONDS),
                new DateTime(TWENTY_TWO_HOURS_MILLISECONDS))).thenReturn(1);
        eventTimeFormatter.formatEventDate(TWENTY_TWO_HOURS_MILLISECONDS);
        verify(timeTextFormatter).getYesterdayFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowTomorrowFormatIfDateBetween24And48HoursAndItsTomorow() {
        when(timeUtils.getCurrentTime()).thenReturn(EIGHT_HOURS_MILLISECONDS);
        when(timeTextFormatter.calculateDaysOfDifferenceBetweenDates(
                new DateTime(EIGHT_HOURS_MILLISECONDS),
                new DateTime(NEXT_DAY_ELEVEN_HOURS_MILLISECONDS))).thenReturn(1);
        eventTimeFormatter.formatEventDate(NEXT_DAY_ELEVEN_HOURS_MILLISECONDS);
        verify(timeTextFormatter).getTomorrowFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowCurrentWeekFormatIfDateBetween24And48HoursAndItsAnotherDay() {
        when(timeUtils.getCurrentTime()).thenReturn(EIGHT_HOURS_MILLISECONDS);
        eventTimeFormatter.formatEventDate(TWO_DAYS_LATER_MILLISECONDS);
        verify(timeTextFormatter).getCurrentWeekFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowDaysAgoFormatIfDateBetween24And48HoursAndItsPast() {
        when(timeUtils.getCurrentTime()).thenReturn(TWO_DAYS_LATER_MILLISECONDS);
        eventTimeFormatter.formatEventDate(EIGHT_HOURS_MILLISECONDS);
        verify(timeTextFormatter).getDaysAgoFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowAnotherYearFormatIfDateIsOnNextYear() {
        when(timeUtils.getCurrentTime()).thenReturn(EIGHT_HOURS_MILLISECONDS);
        eventTimeFormatter.formatEventDate(DATE_ON_NEXT_YEAR);
        verify(timeTextFormatter).getAnotherYearFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowAnotherYearFormatIfDateWasAPastYear() {
        when(timeUtils.getCurrentTime()).thenReturn(EIGHT_HOURS_MILLISECONDS);
        when(timeTextFormatter.calculateDaysOfDifferenceBetweenDates(
                new DateTime(EIGHT_HOURS_MILLISECONDS),
                new DateTime(PAST_YEAR_MILLISECONDS))).thenReturn(RANDOM_DAYS_OF_DIFFERENCE);
        eventTimeFormatter.formatEventDate(PAST_YEAR_MILLISECONDS);
        verify(timeTextFormatter).getAnotherYearFormat(any(DateTime.class));
    }

    @Test
    public void shouldShowCurrentYearFormatIfDateWasThisYear() {
        when(timeUtils.getCurrentTime()).thenReturn(EIGHT_HOURS_MILLISECONDS);
        when(timeTextFormatter.calculateDaysOfDifferenceBetweenDates(
                new DateTime(EIGHT_HOURS_MILLISECONDS),
                new DateTime(SOME_TIME_AGO_THIS_YEAR))).thenReturn(RANDOM_DAYS_OF_DIFFERENCE);
        eventTimeFormatter.formatEventDate(SOME_TIME_AGO_THIS_YEAR);
        verify(timeTextFormatter).getThisYearFormat(any(DateTime.class));
    }

    //region SetUp
    private Long setUpDateStartingLessThanAMinuteInTimestamp() {
        Long thirtySeconds = Long.valueOf(1000 * 30);
        Long timestamp = new Date().getTime()+thirtySeconds;
        return timestamp;
    }

    private Long setUpDateStartedLessThanAMinuteInTimestamp() {
        Long thirtySeconds = Long.valueOf(1000*30);
        Long timestamp = new Date().getTime()-thirtySeconds;
        return timestamp;
    }

    private Long setUpDateStartingLessThanAnHourInTimestamp() {
        Long thirtyMinutes = Long.valueOf(1000*60*30);
        Long timestamp = new Date().getTime()+thirtyMinutes;
        return timestamp;
    }

    private Long setUpDateStartedLessThanAnHourInTimestamp() {
        Long thirtyMinutes = Long.valueOf(1000*60*30);
        Long timestamp = new Date().getTime()-thirtyMinutes;
        return timestamp;
    }

    private Long setUpDateStartingLessThanTwelveHoursAndGreaterThanAnHourInTimestamp() {
        Long hourAndAHalf = Long.valueOf(1000*60*60 + 1000*60*30);
        Long timestamp = new Date().getTime()+hourAndAHalf;
        return timestamp;
    }

    private Long setUpDateStaredLessThanTwelveHoursAndGreaterThanAnHourInTimestamp() {
        Long hourAndAHalf = Long.valueOf(1000*60*60 + 1000*60*30);
        Long timestamp = new Date().getTime()-hourAndAHalf;
        return timestamp;
    }
    //endregion

}
