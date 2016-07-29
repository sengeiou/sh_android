package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMentionedPeopleInteractorTest {

    public static final String MENTION = "mention";
    public static final String ID_USER = "idUser";
    private GetMentionedPeopleInteractor interactor;
    @Mock UserRepository remoteUserRepository;
    @Mock UserRepository localUserRepository;
    @Mock SessionRepository sessionRepository;
    @Mock Interactor.Callback<List<User>> callback;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetMentionedPeopleInteractor(interactorHandler,
          postExecutionThread,
          remoteUserRepository,
          localUserRepository,
          sessionRepository);
    }

    @Test public void shouldLoadMentionedPeopleFromLocal() throws Exception {
        interactor.obtainMentionedPeople(MENTION, callback);

        verify(localUserRepository).getLocalPeople(anyString());
    }

    @Test public void shouldNotLoadFromRemoteIfThereArePeopleInLocal() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(localUserRepository.getLocalPeople(ID_USER)).thenReturn(Collections.singletonList(user()));

        interactor.obtainMentionedPeople(MENTION, callback);

        verify(remoteUserRepository, never()).getPeople();
    }

    @Test public void shouldLoadFromRemoteIfTheresNoLocalPeople() throws Exception {
        interactor.obtainMentionedPeople(MENTION, callback);

        verify(remoteUserRepository).getPeople();
    }

    private User user() {
        User user = new User();
        user.setIdUser("idUser");
        user.setUsername("mentioned_username");
        return user;
    }
}
