package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        //TODO MENTIONS
    }

    private User user() {
        User user = new User();
        user.setIdUser("idUser");
        user.setUsername("mentioned_username");
        return user;
    }
}
