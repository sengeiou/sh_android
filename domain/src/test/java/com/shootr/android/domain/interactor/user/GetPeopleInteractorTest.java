package com.shootr.android.domain.interactor.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.UserList;
import com.shootr.android.domain.exception.RepositoryException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetPeopleInteractorTest {

    private GetPeopleInteractor getPeopleInteractor;

    @Mock TestInteractorHandler interactorHandler;
    @Mock UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        getPeopleInteractor = new GetPeopleInteractor(interactorHandler, userRepository, userRepository);
    }

    @Test
    public void resultsAreSortedByUsername() throws Exception {
        setupHandlerRealExecution();
        setupRepositoryReturnsUserList();

        getPeopleInteractor.obtainPeople();

        ArgumentCaptor<UserList> argumentCaptor = ArgumentCaptor.forClass(UserList.class);
        verify(interactorHandler, atLeastOnce()).sendUiMessage(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getUsers()).isSortedAccordingTo(new TestUsernameComparator());
    }

    private void setupHandlerRealExecution() {
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
    }

    private void setupRepositoryReturnsUserList() {
        when(userRepository.getPeople()).thenReturn(mockUserList());
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