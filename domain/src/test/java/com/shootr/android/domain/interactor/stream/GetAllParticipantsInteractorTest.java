package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.interactor.user.GetAllParticipantsInteractor;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllParticipantsInteractorTest {

    public static final String ID_STREAM = "idStream";
    public static final long MAX_JOIN_DATE = 0L;
    public static final boolean NOT_PAGINATING = false;
    public static final String ID_USER_1 = "idUser1";
    public static final String ID_USER_2 = "idUser2";
    public static final long ANOTHER_JOIN_DATE = 1L;
    public static final String ID_USER_3 = "idUser3";
    public static final boolean IS_PAGINATING = true;
    @Mock UserRepository remoteUserRepository;
    @Mock UserRepository localUserRepository;
    @Mock Interactor.Callback<List<User>> callback;
    @Mock Interactor.ErrorCallback errorCallback;

    private GetAllParticipantsInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetAllParticipantsInteractor(interactorHandler, remoteUserRepository, postExecutionThread);
    }

    @Test
    public void shouldCallbackUserFollowedAndNotFollowedInStreamWhenNotPaginating() {
        when(remoteUserRepository.getPeople()).thenReturn(myFollowings());
        when(remoteUserRepository.getAllParticipants(ID_STREAM, MAX_JOIN_DATE)).thenReturn(allParticipantsInStream());

        interactor.obtainAllParticipants(ID_STREAM, MAX_JOIN_DATE, NOT_PAGINATING, callback, errorCallback);

        verify(callback).onLoaded(participantsAndMyFollowingsInStream());
    }

    @Test
    public void shouldCallbackUserFollowedAndNotFollowedInStreamWhenPaginating() {
        when(remoteUserRepository.getPeople()).thenReturn(myFollowings());
        when(remoteUserRepository.getAllParticipants(ID_STREAM, MAX_JOIN_DATE)).thenReturn(allParticipantsInStream());

        interactor.obtainAllParticipants(ID_STREAM, MAX_JOIN_DATE, IS_PAGINATING, callback, errorCallback);

        verify(callback).onLoaded(participantsAndMyFollowingsInStream());
    }

    @Test
    public void shouldHaveUsersOrderedByJoinStreamDateWhenAllParticipantsLoaded() {
        when(remoteUserRepository.getPeople()).thenReturn(myFollowings());
        when(remoteUserRepository.getAllParticipants(ID_STREAM, MAX_JOIN_DATE)).thenReturn(allParticipantsInStream());

        interactor.obtainAllParticipants(ID_STREAM, MAX_JOIN_DATE, NOT_PAGINATING, callback, errorCallback);

        verify(callback).onLoaded(participantsAndMyFollowingsInStream());
    }

    private List<User> participantsAndMyFollowingsInStream() {
        List<User> people = new ArrayList<>();
        people.add(anotherUser());
        people.add(oneUser());
        return people;
    }

    private List<User> allParticipantsInStream() {
        List<User> people = new ArrayList<>();
        people.add(oneUser());
        people.add(anotherUser());
        return people;
    }

    private List<User> myFollowings() {
        List<User> people = new ArrayList<>();
        people.add(oneUser());
        people.add(userNotInStream());
        return people;
    }

    private User oneUser() {
        User user = new User();
        user.setIdUser(ID_USER_1);
        user.setJoinStreamDate(MAX_JOIN_DATE);
        return user;
    }

    private User anotherUser() {
        User user = new User();
        user.setIdUser(ID_USER_2);
        user.setJoinStreamDate(ANOTHER_JOIN_DATE);
        return user;
    }

    private User userNotInStream() {
        User user = new User();
        user.setIdUser(ID_USER_3);
        return user;
    }
}
