package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReactiveSearchPeopleInteractorTest {

  public static final String QUERY = "query";
  public static final String ID_USER = "id_user";
  private ReactiveSearchPeopleInteractor reactiveSearchPeopleInteractor;
  @Mock UserRepository localUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.Callback<List<User>> callback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    reactiveSearchPeopleInteractor =
        new ReactiveSearchPeopleInteractor(interactorHandler, postExecutionThread,
            localUserRepository, sessionRepository);
  }

  //TODO: tests!

  @Test
  public void shouldGetCurrentUserIdFromSession() throws Exception {
    reactiveSearchPeopleInteractor.obtainPeople(QUERY, callback);

    verify(sessionRepository).getCurrentUserId();
  }

  @Test
  public void shouldGetLocalPeopleFromLocal() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

    reactiveSearchPeopleInteractor.obtainPeople(QUERY, callback);

    verify(localUserRepository).getLocalPeople(ID_USER);
  }

  @Test
  public void shouldLoadPeoplePossiblyMentioned() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getLocalPeople(ID_USER)).thenReturn(usersMentioned());

    reactiveSearchPeopleInteractor.obtainPeople(QUERY, callback);

    verify(callback).onLoaded(anyList());
  }

  @Test
  public void shouldReturnEmptyListIfNoUserMentioned() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getLocalPeople(ID_USER)).thenReturn(usersNotMentioned());

    reactiveSearchPeopleInteractor.obtainPeople(QUERY, callback);

    verify(callback).onLoaded(Collections.<User>emptyList());
  }

  private List<User> usersNotMentioned() {
    User otherUser = new User();
    otherUser.setName("name");
    otherUser.setUsername("name");
    return Collections.singletonList(otherUser);
  }

  private List<User> usersMentioned() {
    User user = new User();
    user.setName(QUERY);
    user.setUsername(QUERY);

    User otherUser = new User();
    otherUser.setName("name");
    otherUser.setUsername("name");
    return Arrays.asList(user, otherUser);
  }
}
