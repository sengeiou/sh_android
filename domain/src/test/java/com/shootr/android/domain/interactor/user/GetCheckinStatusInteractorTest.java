package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class GetCheckinStatusInteractorTest {

    private GetCheckinStatusInteractor interactor;

    @Mock SessionRepository sessionRepository;
    @Mock UserRepository localUserRepository;
    @Spy
    SpyCallback spyCallback = new SpyCallback();

    private String dummyIdEvent = "EVENT_ID";

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetCheckinStatusInteractor(interactorHandler, postExecutionThread,
          localUserRepository,
          sessionRepository);
    }

    @Test public void shouldCallbackFalseWhenCheckinInUserIsFalse() throws Exception {
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUserWithoutCheckin());

        interactor.loadCheckinStatus(spyCallback);

        assertThat(spyCallback.result).isFalse();
    }
    @Test public void shouldCallbackTrueWhenCheckinInUserIsTrue() throws Exception {
        when(localUserRepository.getUserById(anyString())).thenReturn(currentUserWithCheckin());

        interactor.loadCheckinStatus(spyCallback);

        assertThat(spyCallback.result).isTrue();
    }

    private User currentUserWithCheckin() {
        User user = new User();
        user.setIdWatchingEvent(dummyIdEvent);
        user.setIdCheckedEvent(dummyIdEvent);
        return user;
    }

    private User currentUserWithoutCheckin() {
        User user = new User();
        user.setIdWatchingEvent(dummyIdEvent);
        return user;
    }

    private class SpyCallback implements Interactor.Callback<Boolean> {

        Boolean result;

        @Override public void onLoaded(Boolean result) {
            this.result = result;
        }
    }
}