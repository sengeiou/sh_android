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
import com.shootr.android.domain.utils.TimeUtils;
import java.util.Date;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectEventInteractorTest {

    private static final Long OLD_EVENT_ID = 1L;
    private static final Long NEW_EVENT_ID = 2L;
    private static final Long CURRENT_USER_ID = 1L;

    @Mock TestInteractorHandler interactorHandler;
    @Mock EventRepository eventRepository;
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock SelectEventInteractor.Callback dummyCallback;

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

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        verify(sessionRepository).setCurrentUser(currentUserWatchingNewEvent());
    }

    @Test
    public void shouldSetNewEventIdInLocalRepository() throws Exception {
        setupOldVisibleEvent();

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        verify(localUserRepository).putUser(currentUserWatchingNewEvent());
    }

    @Test
    public void shouldSetNewEventIdInRemoteRepository() throws Exception {
        setupOldVisibleEvent();

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        verify(remoteUserRepository).putUser(currentUserWatchingNewEvent());
    }

    @Test @Ignore
    public void selectedEventSavedInLocalIfNotExists() throws Exception {
        when(eventRepository.getEventById(NEW_EVENT_ID)).thenReturn(newEvent());
    }

    @Test
    public void selectingCurrentEventDoesntNotifyUi() throws Exception {
        setupOldVisibleEvent();

        interactor.selectEvent(OLD_EVENT_ID, dummyCallback);

        verify(interactorHandler, never()).sendUiMessage(anyObject());
    }

    @Test
    public void shouldNotifyCallbackBeforeSettingUserInRemoteRepository() throws Exception {
        InOrder inOrder = inOrder(dummyCallback, remoteUserRepository);

        interactor.selectEvent(NEW_EVENT_ID, dummyCallback);

        inOrder.verify(dummyCallback).onLoaded(anyLong());
        inOrder.verify(remoteUserRepository).putUser(any(User.class));
    }

    private void setupOldVisibleEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatchingOldEvent());
    }

    private User currentUserWatchingOldEvent() {
        User user = currentUser();
        user.setVisibleEventId(OLD_EVENT_ID);
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
        return event;
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        return user;
    }
    //endregion
}
