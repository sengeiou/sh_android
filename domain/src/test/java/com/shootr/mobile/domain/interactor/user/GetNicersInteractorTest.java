package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.shot.Nicer;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.NicerRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetNicersInteractorTest {

  private static final String SHOT_ID = "idShot";
  @Mock NicerRepository nicerRepository;
  @Mock Interactor.Callback<List<User>> callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private GetNicersInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();

    interactor = new GetNicersInteractor(interactorHandler, nicerRepository, postExecutionThread);
  }

  @Test public void shouldNotifyLoadedWhenObtainNicersList() throws Exception {
    when(nicerRepository.getNicersWithUser(anyString())).thenReturn(nicers());

    interactor.obtainNicersWithUser(SHOT_ID, callback, errorCallback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyErrorWhenNicerRepositoryThrowServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(nicerRepository)
        .getNicersWithUser(anyString());

    interactor.obtainNicersWithUser(SHOT_ID, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  private List<Nicer> nicers() {
    User user = new User();

    Nicer nicer = new Nicer();
    nicer.setUser(user);

    return Collections.singletonList(nicer);
  }
}
