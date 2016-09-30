package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.follow.FollowRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
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
    @Mock GetMutualsInteractor getMutualsInteractor;
    @Mock Interactor.Callback<List<User>> callback;
    @Mock FollowRepository remoteFollowRepository;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestInteractorHandler interactorHandler = new TestInteractorHandler();
        TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getMutualsInteractor = new GetMutualsInteractor(interactorHandler,
          postExecutionThread,
          remoteUserRepository,
          localUserRepository,
          localFollowRepository,
          remoteFollowRepository);
    }

    @Test public void shouldObtainMutualIdUsers() throws Exception {
        when(remoteFollowRepository.getMutualIdUsers()).thenThrow(serverCommunicationException());

        getMutualsInteractor.obtainMutuals(callback);

        verify(localFollowRepository).getMutualIdUsers();
    }

    @Test public void shouldGetUsersFromLocalRepositoryWhenExceptionThrown() throws Exception {
        when(remoteFollowRepository.getMutualIdUsers()).thenThrow(serverCommunicationException());

        getMutualsInteractor.obtainMutuals(callback);

        verify(localUserRepository).getPeople();
    }

    @Test public void shouldGetUsersFromRemoteRepository() throws Exception {
        getMutualsInteractor.obtainMutuals(callback);

        verify(remoteUserRepository).getPeople();
    }

    @Test public void shouldGetUsersFromRemoteFirstAndThenLocalRepositoryWhenExceptionThrown() throws Exception {
        when(remoteUserRepository.getPeople()).thenThrow(serverCommunicationException());

        getMutualsInteractor.obtainMutuals(callback);

        InOrder inOrder = inOrder(remoteUserRepository, localUserRepository);
        inOrder.verify(remoteUserRepository).getPeople();
        inOrder.verify(localUserRepository).getPeople();
    }

    private ServerCommunicationException serverCommunicationException() {
        return new ServerCommunicationException(new Throwable());
    }

    @Test public void shouldRetainMutuals() throws Exception {
        User mutual = new User();
        mutual.setIdUser(ID_USER);
        List<User> people = people();
        people.add(mutual);
        when(remoteFollowRepository.getMutualIdUsers()).thenReturn(mutualIdUsers());
        when(remoteUserRepository.getPeople()).thenReturn(people);

        getMutualsInteractor.obtainMutuals(callback);

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