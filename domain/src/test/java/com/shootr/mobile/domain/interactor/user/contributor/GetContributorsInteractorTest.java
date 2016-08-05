package com.shootr.mobile.domain.interactor.user.contributor;

import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class GetContributorsInteractorTest {

  private static final String STREAM_ID = "streamId";
  private static final boolean USER_EMBED = true;
  private static final boolean NO_USER_EMBED = false;
  @Mock ContributorRepository contributorRepository;
  @Mock PostExecutionThread postExecutionThread;
  @Mock Interactor.Callback<List<Contributor>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private GetContributorsInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor = new GetContributorsInteractor(interactorHandler, contributorRepository,
        postExecutionThread);
  }

  @Test public void shouldGetContributorsWithUsersrWhenObtainContributorsWithUserEmbed()
      throws Exception {
    interactor.obtainContributors(STREAM_ID, USER_EMBED, callback, errorCallback);

    verify(contributorRepository).getContributorsWithUsers(anyString());
  }

  @Test public void shouldGetContributorsWithoutUsersrWhenObtainContributorsWithoutUserEmbed()
      throws Exception {
    interactor.obtainContributors(STREAM_ID, NO_USER_EMBED, callback, errorCallback);

    verify(contributorRepository).getContributors(anyString());
  }

  @Test public void shouldNotifyErrorWhenCOntributorRepositoryThrowsServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(contributorRepository)
        .getContributorsWithUsers(anyString());

    interactor.obtainContributors(STREAM_ID, USER_EMBED, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }
}
