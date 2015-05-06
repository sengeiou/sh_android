package com.shootr.android.utils;

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

public class EventTimeFormatterTest {

    @Mock
    TimeTextFormatter timeTextFormatter;

    EventTimeFormatter eventTimeFormatter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eventTimeFormatter = new EventTimeFormatter();
    }

    @Test
    public void shouldShowStartingNowIfDateIsInLessThanAMinute() {
        Long timestamp = setUpDateStartingLessThanAMinuteInTimestamp();
        eventTimeFormatter.eventResultDateText(timestamp);
        verify(timeTextFormatter).isOneMinuteRemaining(any(DateTime.class));
    }

    private Long setUpDateStartingLessThanAMinuteInTimestamp() {
        Long thirtySeconds = Long.valueOf(1000*30);
        return new Date().getTime()+thirtySeconds;
    }
}
