package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.Contributor;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindContributorInteractorTest {

  private static final String ID_STREAM = "idStream";
  private static final String QUERY = "query";
  private static final Integer PAGE = 1;
  private static final String ID_USER = "userId";
  @Mock ContributorRepository contributorRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock LocaleProvider localeProvider;
  @Mock Interactor.Callback<List<User>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private FindContributorsInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor = new FindContributorsInteractor(interactorHandler, contributorRepository,
        postExecutionThread, remoteUserRepository, localeProvider);
  }

  @Test public void shouldNotifyRemoteContributorsWhenCallFindRemoteCOntributors()
      throws Exception {
    when(remoteUserRepository.findFriends(anyString(), anyInt(), anyString())).thenReturn(users());
    when(contributorRepository.getContributorsWithUsers(ID_STREAM)).thenReturn(contributors());

    interactor.findContributors(ID_STREAM, QUERY, PAGE, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifiyErrorWhenRemoteUserRepositoryThrowsServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(remoteUserRepository)
        .findFriends(anyString(), anyInt(), anyString());

    interactor.findContributors(ID_STREAM, QUERY, PAGE, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  private List<User> users() {
    return new ArrayList<>();
  }

  private User user() {
    User user = new User();
    user.setIdUser(ID_USER);
    return user;
  }

  private List<Contributor> contributors() {
    Contributor contributor = new Contributor();
    contributor.setUser(user());
    contributor.setIdUser(ID_USER);

    return Collections.singletonList(contributor);
  }
}
