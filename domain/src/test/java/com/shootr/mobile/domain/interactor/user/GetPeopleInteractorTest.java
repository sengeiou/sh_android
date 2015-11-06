package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.UserList;
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
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetPeopleInteractorTest {

    @Spy TestInteractorHandler interactorHandler = new TestInteractorHandler();
    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    private GetPeopleInteractor getPeopleInteractor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        getPeopleInteractor = new GetPeopleInteractor(interactorHandler, remoteUserRepository, localUserRepository);
    }

    @Test public void resultsAreSortedByUsername() throws Exception {
        setupRepositoryReturnsUserList();

        getPeopleInteractor.obtainPeople();

        ArgumentCaptor<UserList> argumentCaptor = ArgumentCaptor.forClass(UserList.class);
        verify(interactorHandler, atLeastOnce()).sendUiMessage(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getUsers()).isSortedAccordingTo(new TestUsernameComparator());
    }

    @Test public void shouldCallbackOnceWhenNoPeopleReturned() throws Exception {
        when(localUserRepository.getPeople()).thenReturn(emptyUserList());
        when(remoteUserRepository.getPeople()).thenReturn(emptyUserList());

        getPeopleInteractor.obtainPeople();

        verify(interactorHandler).sendUiMessage(anyObject());
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