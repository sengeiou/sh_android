package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamSearchResult;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.repository.WatchersRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.mobile.domain.asserts.UserAssert.assertThat;
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
    @Mock StreamRepository remoteStreamRepository;
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock Interactor.Callback<StreamSearchResult> dummyCallback;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock TimeUtils timeUtils;
    @Mock WatchersRepository localWatchersRepository;

    private SelectStreamInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
        when(localUserRepository.getUserById(CURRENT_USER_ID)).thenReturn(currentUser());
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
        interactor = new SelectStreamInteractor(interactorHandler,
          postExecutionThread,
          localStreamRepository, remoteStreamRepository, localUserRepository,
          remoteUserRepository,
          localWatchersRepository,
          sessionRepository,
          timeUtils);
    }

    @Test public void shouldSetNewStreamIdInSessionRepository() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());

        interactor.selectStream(NEW_STREAM_ID, dummyCallback, errorCallback);

        verify(sessionRepository).setCurrentUser(currentUserWatchingNewStream());
    }

    @Test public void shouldSetNewStreamIdInLocalRepository() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());

        interactor.selectStream(NEW_STREAM_ID, dummyCallback, errorCallback);

        verify(localUserRepository).updateWatch(currentUserWatchingNewStream());
    }

    @Test public void shouldSetNewStreamIdInRemoteRepository() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());

        interactor.selectStream(NEW_STREAM_ID, dummyCallback, errorCallback);

        verify(remoteUserRepository).updateWatch(currentUserWatchingNewStream());
    }

    @Test public void selectingCurrentStreamDoesNotifyUi() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(OLD_STREAM_ID)).thenReturn(oldStream());

        interactor.selectStream(OLD_STREAM_ID, dummyCallback, errorCallback);

        verify(dummyCallback).onLoaded(anyStream());
    }

    @Test public void shouldNotUpdateWatchInLocalOrRemoteRepositoryWhenSelectingCurrentStream() throws Exception {
        setupOldWatchingStream();
        when(localStreamRepository.getStreamById(OLD_STREAM_ID)).thenReturn(oldStream());

        interactor.selectStream(OLD_STREAM_ID, dummyCallback, errorCallback);

        verify(localUserRepository, never()).updateWatch(any(User.class));
        verify(remoteUserRepository, never()).updateWatch(any(User.class));
    }

    @Test public void shouldNotifyCallbackBeforeSettingWatchInRemoteRepository() throws Exception {
        when(localStreamRepository.getStreamById(NEW_STREAM_ID)).thenReturn(newStream());
        InOrder inOrder = inOrder(dummyCallback, remoteUserRepository);

        interactor.selectStream(NEW_STREAM_ID, dummyCallback, errorCallback);

        inOrder.verify(dummyCallback).onLoaded(anyStream());
        inOrder.verify(remoteUserRepository).updateWatch(any(User.class));
    }

    @Test public void shouldSetStreamIdWhenUpdateWatchWithStreamInfo() throws Exception {
        User userWithOldStream = currentUserWatchingOldStream();
        Stream selectedStream = newStream();

        User updatedUser = interactor.updateUserWithStreamInfo(userWithOldStream, selectedStream);

        assertThat(updatedUser).hasWatchingStreamId(NEW_STREAM_ID);
    }

    @Test public void should_setStreamTitle_when_updateWatchWithStreamInfo() throws Exception {
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
