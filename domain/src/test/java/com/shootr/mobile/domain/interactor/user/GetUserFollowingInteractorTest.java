package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserFollowingInteractorTest {
  public static final String ID_USER = "id_user";
  public static final int PAGE = 0;
  public static final int PAGE_SIZE = 50;
  public static final int FOLLOWING = 2;
  public static final String ANOTHER_ID_USER = "another_id_user";
  private GetUserFollowingInteractor getUserFollowingInteractor;
  @Mock UserRepository remoteUserRepository;
  @Mock UserRepository localUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.Callback<List<User>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getUserFollowingInteractor =
        new GetUserFollowingInteractor(interactorHandler, postExecutionThread, remoteUserRepository,
            localUserRepository, sessionRepository);
  }

  @Test
  public void shouldGetFollowingFromRemote() throws Exception {
    getUserFollowingInteractor.obtainFollowing(ID_USER, PAGE, callback, errorCallback);

    verify(remoteUserRepository).getFollowing(ID_USER, PAGE, PAGE_SIZE);
  }

  @Test
  public void shouldGetCurrentUserIdFromSession() throws Exception {
    getUserFollowingInteractor.obtainFollowing(ID_USER, PAGE, callback, errorCallback);

    verify(sessionRepository).getCurrentUserId();
  }

  @Test
  public void shouldGetPeopleFromLocal() throws Exception {
    getUserFollowingInteractor.obtainFollowing(ID_USER, PAGE, callback, errorCallback);

    verify(localUserRepository).getPeople();
  }

  @Test
  public void shouldNotifyErrorWhenRemoteThrowsServerCommunicationException() throws Exception {
    when(remoteUserRepository.getFollowing(anyString(), anyInt(), anyInt())).thenThrow(
        new ServerCommunicationException(new Throwable()));

    getUserFollowingInteractor.obtainFollowing(ID_USER, PAGE, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test
  public void shouldNotifyLoadedEmptyListWhenNoFollowing() throws Exception {
    when(remoteUserRepository.getFollowing(anyString(), anyInt(), anyInt())).thenReturn(
        Collections.<User>emptyList());
    when(localUserRepository.getPeople()).thenReturn(Collections.<User>emptyList());
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

    getUserFollowingInteractor.obtainFollowing(ID_USER, PAGE, callback, errorCallback);

    verify(callback).onLoaded(Collections.<User>emptyList());
  }

  @Test
  public void shouldNotifyLoadedUsersWhenFollowingOtherUsers() throws Exception {
    when(remoteUserRepository.getFollowing(anyString(), anyInt(), anyInt())).thenReturn(users());
    when(localUserRepository.getPeople()).thenReturn(users());
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

    getUserFollowingInteractor.obtainFollowing(ID_USER, PAGE, callback, errorCallback);

    verify(callback).onLoaded(users());
  }

  private List<User> users() {
    User user = new User();
    user.setIdUser(ANOTHER_ID_USER);
    user.setRelationship(FOLLOWING);
    return Collections.singletonList(user);
  }
}
