package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMutualsInteractorTest {

    public static final String ID_USER = "ID_USER";
    @Mock UserRepository remoteUserRepository;
    @Mock UserRepository localUserRepository;
    @Mock FollowRepository localFollowRepository;
    private Interactor.ErrorCallback errorCallback;
    @Mock GetMutualsInteractor getMutualsInteractor;
    @Mock Interactor.Callback<List<User>> callback;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestInteractorHandler interactorHandler = new TestInteractorHandler();
        TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getMutualsInteractor = new GetMutualsInteractor(interactorHandler, postExecutionThread, remoteUserRepository, localUserRepository, localFollowRepository);
    }

    @Test public void shouldObtainMutualIdUsers() throws Exception {
        getMutualsInteractor.obtainMutuals(callback, errorCallback);

        verify(localFollowRepository).getMutualIdUsers();
    }

    @Test public void shouldGetUsersFromLocalRepository() throws Exception {
        getMutualsInteractor.obtainMutuals(callback, errorCallback);

        verify(localUserRepository).getPeople();
    }

    @Test public void shouldGetUsersFromRemoteRepository() throws Exception {
        getMutualsInteractor.obtainMutuals(callback, errorCallback);

        verify(remoteUserRepository).getPeople();
    }

    @Test public void shouldGetUsersFromLocalFirstAndThenRemoteRepository() throws Exception {
        getMutualsInteractor.obtainMutuals(callback, errorCallback);

        InOrder inOrder = inOrder(localUserRepository, remoteUserRepository);
        inOrder.verify(localUserRepository).getPeople();
        inOrder.verify(remoteUserRepository).getPeople();
    }

    @Test public void shouldRetainMutuals() throws Exception {
        User mutual = new User();
        mutual.setIdUser(ID_USER);
        List<User> people = people();
        people.add(mutual);
        when(localFollowRepository.getMutualIdUsers()).thenReturn(mutualIdUsers());
        when(remoteUserRepository.getPeople()).thenReturn(people);

        getMutualsInteractor.obtainMutuals(callback, errorCallback);

        verify(callback).onLoaded(Collections.singletonList(mutual));
    }

    private List<String> mutualIdUsers() {
        return Collections.singletonList(ID_USER);
    }

    private List<User> people() {
        List<User> people = new ArrayList<>();
        people.add(new User());
        return people;
    }
}