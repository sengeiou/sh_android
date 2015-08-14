package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.StreamRepository;
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

    private static final String OLD_STREAM_ID = "old_stream";
    private static final String NEW_STREAM_ID = "new_stream";
    private static final String CURRENT_USER_ID = "current_user";
    private static final String OLD_STREAM_TITLE = "oldTitle";
    private static final String NEW_STREAM_TITLE = "newTitle";

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
    public void shouldSetNewStreamIdInSessionRepository() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());

        interactor.selectStream(NEW_STREAM_ID, dummyCallback);

        verify(sessionRepository).setCurrentUser(currentUserWatchingNewStream());
    }

    @Test
    public void shouldSetNewStreamIdInLocalRepository() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());

        interactor.selectStream(NEW_STREAM_ID, dummyCallback);

        verify(localUserRepository).putUser(currentUserWatchingNewStream());
    }

    @Test
    public void shouldSetNewStreamIdInRemoteRepository() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());

        interactor.selectStream(NEW_STREAM_ID, dummyCallback);

        verify(remoteUserRepository).putUser(currentUserWatchingNewStream());
    }

    @Test @Ignore
    public void selectedStreamSavedInLocalIfNotExists() throws Exception {
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());
    }

    @Test
    public void selectingCurrentStreamDoesNotifyUi() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(OLD_STREAM_ID)).thenReturn(oldStream());

        interactor.selectStream(OLD_STREAM_ID, dummyCallback);

        verify(dummyCallback).onLoaded(anyStream());
    }

    @Test public void shouldNotPutUserInLocalOrRemoteRepositoryWhenSelectingCurrentStream() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(OLD_STREAM_ID)).thenReturn(oldStream());

        interactor.selectStream(OLD_STREAM_ID, dummyCallback);

        verify(localUserRepository, never()).putUser(any(User.class));
        verify(remoteUserRepository, never()).putUser(any(User.class));

    }

    @Test
    public void shouldNotifyCallbackBeforeSettingUserInRemoteRepository() throws Exception {
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());
        InOrder inOrder = inOrder(dummyCallback, remoteUserRepository);

        interactor.selectStream(NEW_STREAM_ID, dummyCallback);

        inOrder.verify(dummyCallback).onLoaded(anyStream());
        inOrder.verify(remoteUserRepository).putUser(any(User.class));
    }

    @Test
    public void shouldSetStreamIdWhenUpdateUserWithStreamInfo() throws Exception {
        User userWithOldStream = currentUserWatchingOldStream();
        Stream selectedStream = newStream();

        User updatedUser = interactor.updateUserWithStreamInfo(userWithOldStream, selectedStream);

        assertThat(updatedUser).hasWatchingStreamId(NEW_STREAM_ID);
    }

    @Test
    public void should_setStreamTitle_when_updateUserWithStreamInfo() throws Exception {
        User userWithOldStream = currentUserWatchingOldStream();
        Stream selectedStream = newStream();

        User updatedUser = interactor.updateUserWithStreamInfo(userWithOldStream, selectedStream);

        assertThat(updatedUser).hasVisibleStreamTitle(NEW_STREAM_TITLE);
    }

    private void setupOldWatchingStream() {
        when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(localUserRepository.getUserById(CURRENT_USER_ID)).thenReturn(currentUserWatchingOldStream());
    }

    private User currentUserWatchingOldStream() {
        User user = currentUser();
        user.setIdWatchingStream(OLD_STREAM_ID);
        user.setWatchingStreamTitle(OLD_STREAM_TITLE);
        return user;
    }

    private User currentUserWatchingNewStream() {
        User user = currentUser();
        user.setIdWatchingStream(NEW_STREAM_ID);
        return user;
    }
    //endregion

    //region Stub data
    private Stream newStream() {
        Stream stream = new Stream();
        stream.setId(NEW_STREAM_ID);
        stream.setTitle(NEW_STREAM_TITLE);
        return stream;
    }

    private Stream oldStream() {
        Stream stream = new Stream();
        stream.setId(OLD_STREAM_ID);
        stream.setTitle(OLD_STREAM_TITLE);
        return stream;
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        return user;
    }

    private StreamSearchResult anyStream() {
        return any(StreamSearchResult.class);
    }
    //endregion
}
