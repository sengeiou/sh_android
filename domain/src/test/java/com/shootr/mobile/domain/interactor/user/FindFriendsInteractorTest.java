package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindFriendsInteractorTest {

    private static final String SEARCH = "search";
    private static final Integer PAGE = 3;
    private static final String LOCALE = "locale";
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String STREAM = "stream";
    @Mock InteractorHandler interactorHandler;
    @Mock UserRepository remoteUserRepository;
    @Mock UserRepository localUserRepository;
    @Mock PostExecutionThread postExecutionThread;
    @Mock LocaleProvider localeProvider;
    @Mock Interactor.Callback<List<User>> callback;
    @Mock Interactor.ErrorCallback errorCallback;

    private FindFriendsInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new FindFriendsInteractor(interactorHandler, localUserRepository, remoteUserRepository, postExecutionThread, localeProvider);
    }

    @Test public void shouldOnLoadUsersWhenFindFriends() throws Exception {
        when(remoteUserRepository.findFriends(SEARCH, PAGE, LOCALE)).thenReturn(userList());
        when(localUserRepository.findFriends(SEARCH, PAGE, LOCALE)).thenReturn(userList());

        interactor.findFriends(SEARCH, PAGE, callback, errorCallback);

        verify(callback).onLoaded(anyList());
    }

    @Test public void shouldOnLoadLocalUsersWhenServerComunicationException() throws Exception {
        when(remoteUserRepository.findFriends(SEARCH, PAGE, LOCALE)).thenThrow(ServerCommunicationException.class);
        when(localUserRepository.findFriends(SEARCH, PAGE, LOCALE)).thenReturn(userList());

        interactor.findFriends(SEARCH, PAGE, callback, errorCallback);

        verify(callback).onLoaded(anyList());
    }

    private List<User> userList(){
        ArrayList<User> users = new ArrayList<>();

        users.add(user());

        return users;
    }

    private User user(){
        User user = new User();

        user.setIdUser(USER_ID);
        user.setUsername(USERNAME);
        user.setIdWatchingStream(STREAM);

        return user;
    }
}
