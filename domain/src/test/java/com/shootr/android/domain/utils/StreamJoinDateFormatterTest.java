package com.shootr.android.domain.utils;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StreamJoinDateFormatterTest {

    public static final long RAFA_JOINED =      1440276758493L; // 22:52
    public static final long SCREENSHOT_DATE =  1440279000000L; // 23:30 //diff=38min

    private StreamJoinDateFormatter formatter;

    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        formatter = new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils);
    }

    /**
     * This is the test used to demonstrate that the old formatter was giving wrong results, based on the
     * data captured one night between different platforms in a real life test.
     * We keep it in memory of that tragic night. And to ensure that it doesn't get fucked up again.
     */
    @Test
    public void shouldFormat38MinutesAgoWhenDateIs38MinutesAgoDifferentHour() throws Exception {
        setNow(SCREENSHOT_DATE);

        formatter.format(RAFA_JOINED);

        verify(dateRangeTextProvider).formatMinutesAgo(38);
    }

    //region Just now
    @Test
    public void shouldFormatJustNowWhenDateIsFuture() throws Exception {
        long now = time(22, 0);
        long future = time(22, 30);
        setNow(now);

        formatter.format(future);

        verify(dateRangeTextProvider).formatJustNow();
    }

    @Test
    public void shouldFormatJustNowWhenDateIsNow() throws Exception {
        long now = time(22, 0);
        setNow(now);

        formatter.format(now);

        verify(dateRangeTextProvider).formatJustNow();
    }

    @Test
    public void shouldFormatJustNowWhenDateIs59SecondsAgo() throws Exception {
        long now = time(22, 0, 59);
        long date = time(22, 0, 0);
        setNow(now);

        formatter.format(date);

        verify(dateRangeTextProvider).formatJustNow();
    }
    //endregion

    //region Minutes
    @Test
    public void shouldFormat1MinutesAgoWhenDateIs60SecondsAgo() throws Exception {
        long now = time(22, 1, 0);
        long date = time(22, 0, 0);
        setNow(now);

        formatter.format(date);

        verify(dateRangeTextProvider).formatMinutesAgo(1);
    }

    @Test
    public void shouldFormat59MinutesWhenDateIs59MinutesAgo() throws Exception {
        long now = time(22, 59);
        long date = time(22, 0);
        setNow(now);

        formatter.format(date);

        verify(dateRangeTextProvider).formatMinutesAgo(59);
    }
    //endregion

    //region Days
    @Test
    public void shouldFormat1DayAgoWhenDateIs24HoursAgo() throws Exception {
        long now = date(2, 1, 2015) + time(12, 30);
        long date = date(1, 1, 2015) + time(12, 30);
        setNow(now);

        formatter.format(date);

        verify(dateRangeTextProvider).formatDaysAgo(1);
    }

    @Test
    public void shouldFormat7DaysAgoWhenDateIs7DaysAgo() throws Exception {
        long now = date(10, 1, 1992);
        long date = date(3, 1, 1992);
        setNow(now);

        formatter.format(date);

        verify(dateRangeTextProvider).formatDaysAgo(7);
    }
    //endregion

    @Test
    public void shouldFormatLongTimeWhenDateIs8DaysAgo() throws Exception {
        long now = date(9, 1, 2015);
        long date = date(1, 1, 2015);
        setNow(now);

        formatter.format(date);

        verify(dateRangeTextProvider).formatLongTime();
    }

    //region Date constructors
    private long date(int day, int month, int year) {
        LocalDateTime datetime = LocalDateTime.of(year, month, day, 0, 0, 0);
        return datetime.toEpochSecond(ZoneOffset.UTC)*1000;
    }

    private long time(int hour, int minutes, int seconds) {
        LocalDateTime datetime = LocalDateTime.of(1970, 1, 1, hour, minutes, seconds);
        return datetime.toEpochSecond(ZoneOffset.UTC)*1000;
    }

    private long time(int hour, int minutes) {
        return time(hour, minutes, 0);
    }

    protected void setNow(long date) {
        when(timeUtils.getCurrentDate()).thenReturn(new Date(date));
        when(timeUtils.getCurrentTime()).thenReturn(date);
    }
    //endregion
}