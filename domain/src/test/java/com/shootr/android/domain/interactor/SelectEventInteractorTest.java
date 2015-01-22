package com.shootr.android.domain.interactor;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectEventInteractorTest {

    private static final Long OLD_EVENT_ID = 1L;
    private static final Long NEW_EVENT_ID = 2L;
    private static final Long IRRELEVANT_USER_ID = 1L;
    private static final String EXISTING_STATUS = "status";

    TestInteractorHandler interactorHandler;
    @Mock EventRepository eventRepository;
    @Mock WatchRepository localWatchRepository;
    @Mock WatchRepository remoteWatchRepository;
    @Mock SessionRepository sessionRepository;

    private SelectEventInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(sessionRepository.getCurrentUser()).thenReturn(currentUser());
        interactorHandler = new TestInteractorHandler();
        interactor =
          new SelectEventInteractor(interactorHandler, eventRepository, localWatchRepository, remoteWatchRepository,
            sessionRepository);
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

    private Event newEvent() {
        Event event = new Event();
        event.setId(NEW_EVENT_ID);
        return event;
    }

    //region Setup
    private void setupNewWatchDoesntExist() {
        when(localWatchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID),
          any(ErrorCallback.class))).thenReturn(null);
        when(remoteWatchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID), any(ErrorCallback.class)))
          .thenReturn(null);
    }

    private void setupExistingWatchNotVisibleInLocal() {
        when(localWatchRepository.getWatchForUserAndEvent(any(User.class), eq(NEW_EVENT_ID))).thenReturn(
          existingEventWatchNotVisible());
    }

    private void setupOldVisibleEventInLocal() {
        when(eventRepository.getVisibleEvent()).thenReturn(oldVisibleEvent());
        when(localWatchRepository.getWatchForUserAndEvent(any(User.class), eq(OLD_EVENT_ID),
          any(ErrorCallback.class))).thenReturn(oldVisibleEventWatch());
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
    //endregion

    //region Stub data
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
    //endregion
}
