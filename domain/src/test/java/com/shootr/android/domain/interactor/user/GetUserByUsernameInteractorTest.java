package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidGetUserException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class GetUserByUsernameInteractorTest {

    @Mock
    UserRepository userRepository;
    @Mock
    Interactor.ErrorCallback errorCallback;
    @Mock PostExecutionThread postExecutionThread;

    @Mock Interactor.Callback<User> callback;

    @Mock User user;

    private GetUserByUsernameInteractor interactor;
    private User dummyUser;

    private String DUMMY_USERNAME = "dummy_user";
    private String DUMMY_USERNAME_ID = "\"dummy_user_id\"";
    private String DUMMY_EXCEPTION_MESSAGE = "test";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetUserByUsernameInteractor(interactorHandler, userRepository,
                postExecutionThread);
    }

    @Test public void shouldCallbackIfRepositoryDoesntFail() throws Exception {
        setupUser();

        doReturn(dummyUser).when(userRepository).getUserByUsername(DUMMY_USERNAME);

        interactor.searchUserByUsername(dummyUser.getUsername(), callback, errorCallback);

        verify(callback).onLoaded(any(User.class));
    }

    @Test
    public void shouldCallbackErrorIfRepositoryFailsWithInvalidException() throws Exception {
        setupUser();

        doThrow(new NullPointerException()).when(userRepository).getUserByUsername(DUMMY_USERNAME);

        interactor.searchUserByUsername(user.getUsername(), callback, errorCallback);

        verify(errorCallback).onError(any(ShootrException.class));
    }

    private void setupUser() {
        dummyUser = new User();
        dummyUser.setIdUser(DUMMY_USERNAME_ID);
        dummyUser.setUsername(DUMMY_USERNAME);
    }
}
