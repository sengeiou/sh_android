package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import com.shootr.android.domain.utils.TimeUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.android.domain.asserts.UserAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelectStreamInteractorTest {

    private static final String OLD_EVENT_ID = "old_event";
    private static final String NEW_EVENT_ID = "new_event";
    private static final String CURRENT_USER_ID = "current_user";
    private static final String OLD_EVENT_TITLE = "oldTitle";
    private static final String NEW_EVENT_TITLE = "newTitle";

    @Mock TestInteractorHandler interactorHandler;
    @Mock StreamRepository localStreamRepository;
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock Interactor.Callback<StreamSearchResult> dummyCallback;
    @Mock TimeUtils timeUtils;
    @Mock WatchersRepository localWatchersRepository;

    private SelectStreamInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(localUserRepository.getUserById(CURRENT_USER_ID)).thenReturn(currentUser());
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
        interactor = new SelectStreamInteractor(interactorHandler,
          postExecutionThread, localStreamRepository, localUserRepository,
          remoteUserRepository,
          localWatchersRepository,
          sessionRepository,
          timeUtils);
    }

    @Test
    public void shouldSetNewEventIdInSessionRepository() throws Exception {
        setupOldWatchingEvent();
        when(localStreamRepository.getStreamById(NEW_EVENT_ID)).thenReturn(newEvent());

        interactor.selectStream(NEW_EVENT_ID, dummyCallback);

        verify(sessionRepository).setCurrentUser(currentUserWatchingNewEvent());
    }

    @Test
    public void shouldSetNewEventIdInLocalRepository() throws Exception {
        setupOldWatchingEvent();
        when(localStreamRepository.getStreamById(NEW_EVENT_ID)).thenReturn(newEvent());

        interactor.selectStream(NEW_EVENT_ID, dummyCallback);

        verify(localUserRepository).putUser(currentUserWatchingNewEvent());
    }

    @Test
    public void shouldSetNewEventIdInRemoteRepository() throws Exception {
        setupOldWatchingEvent();
        when(localStreamRepository.getStreamById(NEW_EVENT_ID)).thenReturn(newEvent());

        interactor.selectStream(NEW_EVENT_ID, dummyCallback);

        verify(remoteUserRepository).putUser(currentUserWatchingNewEvent());
    }

    @Test @Ignore
    public void selectedEventSavedInLocalIfNotExists() throws Exception {
        when(localStreamRepository.getStreamById(NEW_EVENT_ID)).thenReturn(newEvent());
    }

    @Test
    public void selectingCurrentEventDoesNotifyUi() throws Exception {
        setupOldWatchingEvent();
        when(localStreamRepository.getStreamById(OLD_EVENT_ID)).thenReturn(oldEvent());

        interactor.selectStream(OLD_EVENT_ID, dummyCallback);

        verify(dummyCallback).onLoaded(anyEvent());
    }

    @Test public void shouldNotPutUserInLocalOrRemoteRepositoryWhenSelectingCurrentEvent() throws Exception {
        setupOldWatchingEvent();
        when(localStreamRepository.getStreamById(OLD_EVENT_ID)).thenReturn(oldEvent());

        interactor.selectStream(OLD_EVENT_ID, dummyCallback);

        verify(localUserRepository, never()).putUser(any(User.class));
        verify(remoteUserRepository, never()).putUser(any(User.class));

    }

    @Test
    public void shouldNotifyCallbackBeforeSettingUserInRemoteRepository() throws Exception {
        when(localStreamRepository.getStreamById(NEW_EVENT_ID)).thenReturn(newEvent());
        InOrder inOrder = inOrder(dummyCallback, remoteUserRepository);

        interactor.selectStream(NEW_EVENT_ID, dummyCallback);

        inOrder.verify(dummyCallback).onLoaded(anyEvent());
        inOrder.verify(remoteUserRepository).putUser(any(User.class));
    }

    @Test
    public void should_setEventId_when_updateUserWithEventInfo() throws Exception {
        User userWithOldEvent = currentUserWatchingOldEvent();
        Stream selectedStream = newEvent();

        User updatedUser = interactor.updateUserWithStreamInfo(userWithOldEvent, selectedStream);

        assertThat(updatedUser).hasWatchingEventId(NEW_EVENT_ID);
    }

    @Test
    public void should_setEventTitle_when_updateUserWithEventInfo() throws Exception {
        User userWithOldEvent = currentUserWatchingOldEvent();
        Stream selectedStream = newEvent();

        User updatedUser = interactor.updateUserWithStreamInfo(userWithOldEvent, selectedStream);

        assertThat(updatedUser).hasVisibleEventTitle(NEW_EVENT_TITLE);
    }

    private void setupOldWatchingEvent() {
        when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(localUserRepository.getUserById(CURRENT_USER_ID)).thenReturn(currentUserWatchingOldEvent());
    }

    private User currentUserWatchingOldEvent() {
        User user = currentUser();
        user.setIdWatchingStream(OLD_EVENT_ID);
        user.setWatchingStreamTitle(OLD_EVENT_TITLE);
        return user;
    }

    private User currentUserWatchingNewEvent() {
        User user = currentUser();
        user.setIdWatchingStream(NEW_EVENT_ID);
        return user;
    }
    //endregion

    //region Stub data
    private Stream newEvent() {
        Stream stream = new Stream();
        stream.setId(NEW_EVENT_ID);
        stream.setTitle(NEW_EVENT_TITLE);
        return stream;
    }

    private Stream oldEvent() {
        Stream stream = new Stream();
        stream.setId(OLD_EVENT_ID);
        stream.setTitle(OLD_EVENT_TITLE);
        return stream;
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        return user;
    }

    private StreamSearchResult anyEvent() {
        return any(StreamSearchResult.class);
    }
    //endregion
}
