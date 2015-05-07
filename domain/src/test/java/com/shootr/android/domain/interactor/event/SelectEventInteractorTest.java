package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.android.domain.asserts.UserAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectEventInteractorTest {

    private static final String OLD_EVENT_ID = "old_event";
    private static final String NEW_EVENT_ID = "new_event";
    private static final String CURRENT_USER_ID = "current_user";
    private static final String OLD_EVENT_TITLE = "oldTitle";
    private static final String NEW_EVENT_TITLE = "newTitle";

    @Mock TestInteractorHandler interactorHandler;
    @Mock EventRepository eventRepository;
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock Interactor.Callback<Event> dummyCallback;

    private SelectEventInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        when(sessionRepository.getCurrentUser()).thenReturn(currentUser());
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
        interactor = new SelectEventInteractor(interactorHandler, postExecutionThread,
          eventRepository,
          localUserRepository,
          remoteUserRepository,
          sessionRepository);
    }

    @Test
    public void shouldSetNewEventIdInSessionRepository() throws Exception {
        setupOldVisibleEvent();
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        verify(sessionRepository).setCurrentUser(currentUserWatchingNewEvent());
    }

    @Test
    public void shouldSetNewEventIdInLocalRepository() throws Exception {
        setupOldVisibleEvent();
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        verify(localUserRepository).putUser(currentUserWatchingNewEvent());
    }

    @Test
    public void shouldSetNewEventIdInRemoteRepository() throws Exception {
        setupOldVisibleEvent();
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        verify(remoteUserRepository).putUser(currentUserWatchingNewEvent());
    }

    @Test @Ignore
    public void selectedEventSavedInLocalIfNotExists() throws Exception {
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());
    }

    @Test
    public void selectingCurrentEventDoesNotifyUi() throws Exception {
        setupOldVisibleEvent();
        when(eventRepository.getEventById(OLD_EVENT_ID)).thenReturn(oldEvent());

        interactor.selectEvent(OLD_EVENT_ID, dummyCallback);

        verify(dummyCallback).onLoaded(anyEvent());
    }

    @Test public void shouldNotPutUserInLocalOrRemoteRepositoryWhenSelectingCurrentEvent() throws Exception {
        setupOldVisibleEvent();
        when(eventRepository.getEventById(OLD_EVENT_ID)).thenReturn(oldEvent());

        interactor.selectEvent(OLD_EVENT_ID, dummyCallback);

        verify(localUserRepository, never()).putUser(any(User.class));
        verify(remoteUserRepository, never()).putUser(any(User.class));

    }

    @Test
    public void shouldNotifyCallbackBeforeSettingUserInRemoteRepository() throws Exception {
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());
        InOrder inOrder = inOrder(dummyCallback, remoteUserRepository);

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        inOrder.verify(dummyCallback).onLoaded(anyEvent());
        inOrder.verify(remoteUserRepository).putUser(any(User.class));
    }

    @Test
    public void should_setEventId_when_updateUserWithEventInfo() throws Exception {
        User userWithOldEvent = currentUserWatchingOldEvent();
        Event selectedEvent = newEvent();

        User updatedUser = interactor.updateUserWithEventInfo(userWithOldEvent, selectedEvent);

        assertThat(updatedUser).hasVisibleEventId(NEW_EVENT_ID);
    }

    @Test
    public void should_setEventTitle_when_updateUserWithEventInfo() throws Exception {
        User userWithOldEvent = currentUserWatchingOldEvent();
        Event selectedEvent = newEvent();

        User updatedUser = interactor.updateUserWithEventInfo(userWithOldEvent, selectedEvent);

        assertThat(updatedUser).hasVisibleEventTitle(NEW_EVENT_TITLE);
    }

    @Test public void shouldSetCheckinAsFalseInLocalRepository() throws Exception {
        setupOldVisibleEvent();
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(localUserRepository).putUser(userCaptor.capture());
        assertThat(userCaptor.getValue().isCheckedIn()).isFalse();
    }

    private void setupOldVisibleEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatchingOldEvent());
    }

    private User currentUserWatchingOldEvent() {
        User user = currentUser();
        user.setVisibleEventId(OLD_EVENT_ID);
        user.setVisibleEventTitle(OLD_EVENT_TITLE);
        return user;
    }

    private User currentUserWatchingNewEvent() {
        User user = currentUser();
        user.setVisibleEventId(NEW_EVENT_ID);
        return user;
    }
    //endregion

    //region Stub data
    private Event newEvent() {
        Event event = new Event();
        event.setId(NEW_EVENT_ID);
        event.setTitle(NEW_EVENT_TITLE);
        return event;
    }

    private Event oldEvent() {
        Event event = new Event();
        event.setId(OLD_EVENT_ID);
        event.setTitle(OLD_EVENT_TITLE);
        return event;
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        return user;
    }

    private Event anyEvent() {
        return any(Event.class);
    }
    //endregion
}
