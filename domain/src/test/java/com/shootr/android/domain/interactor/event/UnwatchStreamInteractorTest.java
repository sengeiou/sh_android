package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shootr.android.domain.interactor.Interactor.CompletedCallback;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnwatchStreamInteractorTest {

    public static final String USER_ID = "user_id";
    public static final String WATCHING_EVENT_ID = "watching_event_id";

    @Mock SessionRepository sessionRepository;
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock CompletedCallback completedCallback;

    private UnwatchEventInteractor unwatchEventInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        this.unwatchEventInteractor = new UnwatchEventInteractor(interactorHandler, postExecutionThread,
          sessionRepository, localUserRepository, remoteUserRepository);
    }

    //region tests
    @Test
    public void shouldUpdateUserInfoInSessionRepositoryWhenUnwatch() throws Throwable {
        setupUserWithWatchingEvent();

        unwatchEventInteractor.unwatchEvent(completedCallback);

        verify(sessionRepository).setCurrentUser(user());
    }

    @Test
    public void shouldUpdateUserInfoInLocalRepositoryWhenUnwatch() {
        setupUserWithWatchingEvent();

        unwatchEventInteractor.unwatchEvent(completedCallback);

        verify(localUserRepository).putUser(user());
    }

    @Test
    public void shouldUpdateUserInfoInRemoteRepositoryWhenUnwatch() {
        setupUserWithWatchingEvent();

        unwatchEventInteractor.unwatchEvent(completedCallback);

        verify(remoteUserRepository).putUser(user());
    }

    @Test
    public void shouldPutUserWithNoWatchingEventInSessionRepositoryWhenUnwatch() {
        setupUserWithWatchingEvent();

        unwatchEventInteractor.unwatchEvent(completedCallback);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(sessionRepository).setCurrentUser(argument.capture());
        assertThat(argument.getValue().getIdWatchingEvent()).isNull();
    }

    @Test
    public void shouldNotifyCompletedAfterLocalUserInfoUpdated() {
        setupUserWithWatchingEvent();

        unwatchEventInteractor.unwatchEvent(completedCallback);

        InOrder inOrder = inOrder(localUserRepository, completedCallback);
        inOrder.verify(localUserRepository).putUser(user());
        inOrder.verify(completedCallback).onCompleted();
    }

    @Test
    public void shouldNotifyCompletedBeforeRemoteUserInfoUpdated() {
        setupUserWithWatchingEvent();

        unwatchEventInteractor.unwatchEvent(completedCallback);

        InOrder inOrder = inOrder(completedCallback, remoteUserRepository);
        inOrder.verify(completedCallback).onCompleted();
        inOrder.verify(remoteUserRepository).putUser(user());
    }
    //endregion

    //region stubbers
    private User user() {
        User user = new User();
        user.setIdUser(USER_ID);
        return user;
    }

    private User userWithWatchingEvent() {
        User user = new User();
        user.setIdUser(USER_ID);
        user.setIdWatchingEvent(WATCHING_EVENT_ID);
        return user;
    }

    //endregion

    private void setupUserWithWatchingEvent() {
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        when(localUserRepository.getUserById(USER_ID)).thenReturn(userWithWatchingEvent());
    }
}
