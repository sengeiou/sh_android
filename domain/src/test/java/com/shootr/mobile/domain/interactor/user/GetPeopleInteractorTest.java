package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.UserList;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetPeopleInteractorTest {

    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock Interactor.Callback<UserList> callback;
    @Mock Interactor.ErrorCallback errorCallback;
    private GetPeopleInteractor getPeopleInteractor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestInteractorHandler interactorHandler = new TestInteractorHandler();
        TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
        getPeopleInteractor =
          new GetPeopleInteractor(interactorHandler, remoteUserRepository, localUserRepository, postExecutionThread);
    }

    @Test public void resultsAreSortedByUsername() throws Exception {
        setupRepositoryReturnsUserList();

        getPeopleInteractor.obtainPeople(callback, errorCallback);

        ArgumentCaptor<UserList> argumentCaptor = ArgumentCaptor.forClass(UserList.class);
        verify(callback, atLeastOnce()).onLoaded(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getUsers()).isSortedAccordingTo(new TestUsernameComparator());
    }

    @Test public void shouldOnlyCallbackWhenNoPeopleReturned() throws Exception {
        when(localUserRepository.getPeople()).thenReturn(emptyUserList());
        when(remoteUserRepository.getPeople()).thenReturn(emptyUserList());

        getPeopleInteractor.obtainPeople(callback, errorCallback);

        verify(callback, only()).onLoaded(any(UserList.class));
    }

    private List<User> emptyUserList() {
        return new ArrayList<>();
    }

    private void setupRepositoryReturnsUserList() {
        when(localUserRepository.getPeople()).thenReturn(mockUserList());
        when(remoteUserRepository.getPeople()).thenReturn(mockUserList());
    }

    private List<User> mockUserList() {
        ArrayList<User> mockUsers = new ArrayList<>();
        mockUsers.add(mockUser("Ausername"));
        mockUsers.add(mockUser("Busername"));
        mockUsers.add(mockUser("cusername"));
        mockUsers.add(mockUser("Dusername"));
        return mockUsers;
    }

    private UserList userList() {
        return new UserList(mockUserList());
    }

    private User mockUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    private class TestUsernameComparator implements Comparator<User> {

        @Override public int compare(User o1, User o2) {
            return o1.getUsername().compareToIgnoreCase(o2.getUsername());
        }
    }
}