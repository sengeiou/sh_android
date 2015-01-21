package com.shootr.android.domain.interactor;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import com.sun.org.apache.bcel.internal.generic.NEW;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectEventInteractorTest {

    private static final Long OLD_EVENT_ID = 1L;
    private static final Long NEW_EVENT_ID = 2L;
    private static final Long IRRELEVANT_USER_ID = 1L;

    TestInteractorHandler interactorHandler;
    @Mock EventRepository eventRepository;
    @Mock WatchRepository watchRepository;
    @Mock SessionRepository sessionRepository;

    private SelectEventInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactorHandler = new TestInteractorHandler();
        interactor = new SelectEventInteractor(interactorHandler, eventRepository, watchRepository, sessionRepository);
    }

    @Test
    public void testOldEventNotVisibleAnymore() throws Exception {
        when(eventRepository.getVisibleEvent()).thenReturn(oldVisibleEvent());
        when(
          watchRepository.getWatchForUserAndEvent(any(User.class), eq(OLD_EVENT_ID), any(ErrorCallback.class))).thenReturn(
          oldVisibleEventWatch());

        interactor.selectEvent(NEW_EVENT_ID);

        ArgumentCaptor<Watch> watchArgumentCaptor = ArgumentCaptor.forClass(Watch.class);
        verify(watchRepository, times(2)).putWatch(watchArgumentCaptor.capture(), any(WatchRepository.WatchCallback.class));

        List<Watch> savedWatches = watchArgumentCaptor.getAllValues();
        Watch oldVisibleWatch = savedWatches.get(0);
        assertThat(oldVisibleWatch.getIdEvent()).isEqualTo(OLD_EVENT_ID);
        assertThat(oldVisibleWatch.isVisible()).isFalse();
    }

    @Test
    public void testNewEventVisibleWhenWatchExist() throws Exception {
        when(watchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID),
          any(ErrorCallback.class))).thenReturn(existingEventwatchNotVisible());

        Watch existingWatch = watchRepository.getWatchForUserAndEvent(null, NEW_EVENT_ID, null);
        assertThat(existingWatch.getIdEvent()).isEqualTo(NEW_EVENT_ID);
        assertThat(existingWatch.isVisible()).isFalse();

        interactor.selectEvent(NEW_EVENT_ID);

        ArgumentCaptor<Watch> watchArgumentCaptor = ArgumentCaptor.forClass(Watch.class);
        verify(watchRepository).putWatch(watchArgumentCaptor.capture(), any(WatchRepository.WatchCallback.class));

        Watch newEventWatch = watchArgumentCaptor.getValue();
        assertThat(newEventWatch.getIdEvent()).isEqualTo(NEW_EVENT_ID);
        assertThat(newEventWatch.isVisible()).isTrue();
    }

    @Test
    public void testNewEventWatchCreatedIfNotExists() throws Exception {
        when(watchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID),
          any(ErrorCallback.class))).thenReturn(null);
        when(sessionRepository.getCurrentUser()).thenReturn(currentUser());

        interactor.selectEvent(NEW_EVENT_ID);

        ArgumentCaptor<Watch> watchArgumentCaptor = ArgumentCaptor.forClass(Watch.class);
        verify(watchRepository).putWatch(watchArgumentCaptor.capture(), any(WatchRepository.WatchCallback.class));

        Watch newEventWatch = watchArgumentCaptor.getValue();
        assertThat(newEventWatch.getIdEvent()).isEqualTo(NEW_EVENT_ID);
        assertThat(newEventWatch.getUser().getIdUser()).isEqualTo(IRRELEVANT_USER_ID);
        assertThat(newEventWatch.isVisible()).isTrue();
        assertThat(newEventWatch.getUserStatus()).isNotEmpty();
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(IRRELEVANT_USER_ID);
        return user;
    }

    private Watch existingEventwatchNotVisible() {
        Watch watch = new Watch();
        watch.setIdEvent(NEW_EVENT_ID);
        watch.setVisible(false);
        return watch;
    }

    private Watch oldVisibleEventWatch() {
        Watch watch = new Watch();
        watch.setVisible(true);
        watch.setIdEvent(OLD_EVENT_ID);
        return watch;
    }

    private Event oldVisibleEvent() {
        Event event = new Event();
        event.setId(OLD_EVENT_ID);
        return event;
    }
}
