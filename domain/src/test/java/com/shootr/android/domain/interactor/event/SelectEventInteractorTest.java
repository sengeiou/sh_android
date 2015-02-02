package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.Date;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectEventInteractorTest {

    private static final Long OLD_EVENT_ID = 1L;
    private static final Long NEW_EVENT_ID = 2L;
    private static final Long IRRELEVANT_USER_ID = 1L;
    private static final Long LAPSED_EVENT_ID = 3L;
    private static final String EXISTING_STATUS = "status";
    private static final Date DATE_NOW = new Date();
    private static final Date DATE_BEFORE = new Date(DATE_NOW.getTime() - 1);

    @Mock TestInteractorHandler interactorHandler;
    @Mock EventRepository eventRepository;
    @Mock WatchRepository localWatchRepository;
    @Mock WatchRepository remoteWatchRepository;
    @Mock TimeUtils timeUtils;
    @Mock SessionRepository sessionRepository;

    private SelectEventInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(timeUtils.getCurrentDate()).thenReturn(DATE_NOW);
        when(timeUtils.getCurrentTime()).thenReturn(DATE_NOW.getTime());
        when(sessionRepository.getCurrentUser()).thenReturn(currentUser());
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
        interactor =
          new SelectEventInteractor(interactorHandler, postExecutionThread, eventRepository, localWatchRepository, remoteWatchRepository,
            sessionRepository, timeUtils);
    }

    @Test
    public void oldEventNotVisibleAnymore() throws Exception {
        setupOldVisibleEventInLocal();

        interactor.selectEvent(NEW_EVENT_ID);

        verifyOldWatchSavedNotVisibleInRepo(localWatchRepository);
        verifyOldWatchSavedNotVisibleInRepo(remoteWatchRepository);
    }

    @Test
    public void newEventVisibleWhenWatchExistInLocal() throws Exception {
        setupExistingWatchNotVisibleInLocal();

        interactor.selectEvent(NEW_EVENT_ID);

        verifyExistingWatchSavedVisibleInRepoWithExistingStatus(localWatchRepository);
        verifyExistingWatchSavedVisibleInRepoWithExistingStatus(remoteWatchRepository);
    }

    @Test
    public void newEventWatchCreatedIfNotExists() throws Exception {
        setupNewWatchDoesntExist();

        interactor.selectEvent(NEW_EVENT_ID);

        verifyNewVisibleWatchSavedInRepoWithCorrectAttributes(localWatchRepository);
        verifyNewVisibleWatchSavedInRepoWithCorrectAttributes(remoteWatchRepository);
    }

    @Test @Ignore
    public void selectedEventSavedInLocalIfNotExists() throws Exception {
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());
    }

    @Test
    public void oldWatchingEventSetToNotWatchingIfLapsed() throws Exception {
        setupWatchingLapsedEvent();

        interactor.selectEvent(NEW_EVENT_ID);

        verifyLapsedEventSavedNotWatchingInRepo(localWatchRepository);
        verifyLapsedEventSavedNotWatchingInRepo(remoteWatchRepository);
    }

    @Test
    public void selectingCurrentEventDoesntNotifyUi() throws Exception {
        setupOldVisibleEventInLocal();

        interactor.selectEvent(OLD_EVENT_ID);

        verify(interactorHandler, never()).sendUiMessage(anyObject());
    }

    //region Setup
    private void setupNewWatchDoesntExist() {
        when(localWatchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID))).thenReturn(null);
        when(remoteWatchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID)))
          .thenReturn(null);
    }

    private void setupExistingWatchNotVisibleInLocal() {
        when(localWatchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID))).thenReturn(
          existingEventWatchNotVisible());
    }

    private void setupOldVisibleEventInLocal() {
        when(localWatchRepository.getCurrentVisibleWatch()).thenReturn(oldVisibleEventWatch());
    }

    private void setupWatchingLapsedEvent() {
        when(localWatchRepository.getCurrentWatching()).thenReturn(lapsedEventWatchingWatch());
        when(eventRepository.getEventById(eq(LAPSED_EVENT_ID))).thenReturn(lapsedEvent());
    }
    //endregion

    //region Asserts

    private void verifyOldWatchSavedNotVisibleInRepo(WatchRepository watchRepository) {
        ArgumentCaptor<Watch> watchArgumentCaptor = ArgumentCaptor.forClass(Watch.class);
        verify(watchRepository, atLeastOnce()).putWatch(watchArgumentCaptor.capture());
        Watch oldVisibleWatch = watchArgumentCaptor.getAllValues().get(0);
        assertOldWatchNotVisible(oldVisibleWatch);
    }

    private void assertOldWatchNotVisible(Watch oldVisibleWatch) {
        assertThat(oldVisibleWatch.getIdEvent()).isEqualTo(OLD_EVENT_ID);
        assertThat(oldVisibleWatch.isVisible()).isFalse();
    }

    private ArgumentCaptor<Watch> verifyExistingWatchSavedVisibleInRepoWithExistingStatus(
      WatchRepository watchRepository) {
        ArgumentCaptor<Watch> watchArgumentCaptor = ArgumentCaptor.forClass(Watch.class);
        verify(watchRepository).putWatch(watchArgumentCaptor.capture());
        Watch newEventWatch = watchArgumentCaptor.getValue();
        assertNewEventIsVisible(newEventWatch);
        assertThat(newEventWatch.getUserStatus()).isEqualTo(EXISTING_STATUS);
        return watchArgumentCaptor;
    }

    private void assertNewEventIsVisible(Watch newEventWatch) {
        assertThat(newEventWatch.getIdEvent()).isEqualTo(NEW_EVENT_ID);
        assertThat(newEventWatch.isVisible()).isTrue();
    }

    private void verifyNewVisibleWatchSavedInRepoWithCorrectAttributes(WatchRepository watchRepository) {
        ArgumentCaptor<Watch> watchArgumentCaptor = ArgumentCaptor.forClass(Watch.class);
        verify(watchRepository).putWatch(watchArgumentCaptor.capture());
        Watch newEventWatch = watchArgumentCaptor.getValue();
        assertNewWatchHasRightIdUserVisibilityStatus(newEventWatch);
    }

    private void assertNewWatchHasRightIdUserVisibilityStatus(Watch newEventWatch) {
        assertThat(newEventWatch.getIdEvent()).isEqualTo(NEW_EVENT_ID);
        assertThat(newEventWatch.getUser().getIdUser()).isEqualTo(IRRELEVANT_USER_ID);
        assertThat(newEventWatch.isVisible()).isTrue();
        assertThat(newEventWatch.getUserStatus()).isNotEmpty();
    }

    private void verifyLapsedEventSavedNotWatchingInRepo(WatchRepository watchRepository) {
        ArgumentCaptor<Watch> watchArgumentCaptor = ArgumentCaptor.forClass(Watch.class);
        verify(watchRepository, atLeastOnce()).putWatch(watchArgumentCaptor.capture());

        Watch lapsedWatch = watchArgumentCaptor.getAllValues().get(0);
        assertThat(lapsedWatch.getIdEvent()).isEqualTo(LAPSED_EVENT_ID);
        assertThat(lapsedWatch.isWatching()).isFalse();
        assertThat(lapsedWatch.isNotificaticationsEnabled()).isFalse();
    }
    //endregion

    //region Stub data
    private Event newEvent() {
        Event event = new Event();
        event.setId(NEW_EVENT_ID);
        return event;
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(IRRELEVANT_USER_ID);
        return user;
    }

    private Watch existingEventWatchNotVisible() {
        Watch watch = new Watch();
        watch.setIdEvent(NEW_EVENT_ID);
        watch.setVisible(false);
        watch.setUserStatus(EXISTING_STATUS);
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

    private Watch lapsedEventWatchingWatch() {
        Watch watch = new Watch();
        watch.setIdEvent(LAPSED_EVENT_ID);
        watch.setUser(currentUser());
        watch.setWatching(true);
        watch.setNotificaticationsEnabled(true);
        return watch;
    }

    private Event lapsedEvent() {
        Event event = new Event();
        event.setId(LAPSED_EVENT_ID);
        event.setEndDate(DATE_BEFORE);
        return event;
    }
    //endregion
}
