package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetUserCanPinMessageInteractorTest {

  public static final String ID_USER = "id_user";
  public static final String ID_STREAM = "id_stream";
  public static final String STREAM_AUTHOR_ID_USER = "stream_author_id_user";
  private GetUserCanPinMessageInteractor getUserCanPinMessageInteractor;
  @Mock ContributorRepository localContributorRepository;
  @Mock ContributorRepository remoteContributorRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.Callback<Boolean> callback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getUserCanPinMessageInteractor =
        new GetUserCanPinMessageInteractor(interactorHandler, localContributorRepository,
            remoteContributorRepository, sessionRepository, postExecutionThread);
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
  }

  @Test public void shouldGetCurrentUserId() throws Exception {
    getUserCanPinMessageInteractor.canUserPinMessage(ID_STREAM, STREAM_AUTHOR_ID_USER, callback);

    verify(sessionRepository).getCurrentUserId();
  }

  @Test public void shouldGetContributorsFromLocalRepository() throws Exception {
    getUserCanPinMessageInteractor.canUserPinMessage(ID_STREAM, STREAM_AUTHOR_ID_USER, callback);

    verify(localContributorRepository).getContributors(ID_STREAM);
  }

  @Test public void shouldGetContributorsFromRemoteRepository() throws Exception {
    when(localContributorRepository.getContributors(ID_STREAM)).thenReturn(
        Collections.<Contributor>emptyList());

    getUserCanPinMessageInteractor.canUserPinMessage(ID_STREAM, STREAM_AUTHOR_ID_USER, callback);

    verify(localContributorRepository).getContributors(ID_STREAM);
  }

  @Test public void shouldNotifyTrueIfUserIsHolder() throws Exception {
    getUserCanPinMessageInteractor.canUserPinMessage(ID_STREAM, ID_USER, callback);

    verify(callback).onLoaded(true);
  }

  @Test public void shouldNotifyFalseIfUserIsNotHolderOrContributor() throws Exception {
    getUserCanPinMessageInteractor.canUserPinMessage(ID_STREAM, STREAM_AUTHOR_ID_USER, callback);

    verify(callback).onLoaded(false);
  }

  @Test public void shouldNotifyTrueIfUserNotHolderButContributor() throws Exception {
    when(localContributorRepository.getContributors(ID_STREAM)).thenReturn(contributors());

    getUserCanPinMessageInteractor.canUserPinMessage(ID_STREAM, STREAM_AUTHOR_ID_USER, callback);

    verify(callback).onLoaded(true);
  }

  @Test public void shouldNotifyTrueIfUserIsHolderAndContributor() throws Exception {
    when(localContributorRepository.getContributors(ID_STREAM)).thenReturn(contributors());

    getUserCanPinMessageInteractor.canUserPinMessage(ID_STREAM, ID_USER, callback);

    verify(callback).onLoaded(true);
  }

  private List<Contributor> contributors() {
    return Collections.singletonList(contributor(ID_USER));
  }

  private Contributor contributor(String idUser) {
    Contributor contributor = new Contributor();
    contributor.setIdUser(idUser);
    return contributor;
  }
}