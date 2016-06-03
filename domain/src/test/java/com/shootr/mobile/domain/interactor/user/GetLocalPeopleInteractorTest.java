package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetLocalPeopleInteractorTest {
  private GetLocalPeopleInteractor getLocalPeopleInteractor;
  @Mock UserRepository localUserRepository;
  @Mock Interactor.Callback<List<User>> callback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    getLocalPeopleInteractor =
        new GetLocalPeopleInteractor(interactorHandler, localUserRepository, postExecutionThread);
  }

  @Test
  public void shouldGetPeopleFromLocalRepository() throws Exception {
    getLocalPeopleInteractor.obtainPeople(callback);

    verify(localUserRepository).getPeople();
  }

  @Test
  public void shouldNotifyLoadedPeople() throws Exception {
    when(localUserRepository.getPeople()).thenReturn(Collections.<User>emptyList());

    getLocalPeopleInteractor.obtainPeople(callback);

    verify(callback).onLoaded(anyList());
  }
}
