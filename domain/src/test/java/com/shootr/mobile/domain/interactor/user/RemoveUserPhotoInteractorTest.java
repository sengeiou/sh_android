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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveUserPhotoInteractorTest {
  public static final String PHOTO_URL = "photo_url";
  public static final String USER_ID = "user_id";
  private RemoveUserPhotoInteractor removeUserPhotoInteractor;
  @Mock UserRepository localUserRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Captor ArgumentCaptor<User> userArgumentCaptor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    removeUserPhotoInteractor =
        new RemoveUserPhotoInteractor(interactorHandler, postExecutionThread, localUserRepository,
            remoteUserRepository, sessionRepository);
  }

  @Test
  public void shouldUploadUserWithoutPictureInRemote() throws Exception {
    User user = userWithPhoto();
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(localUserRepository.getUserById(USER_ID)).thenReturn(user);

    removeUserPhotoInteractor.removeUserPhoto(callback, errorCallback);

    verify(remoteUserRepository).putUser(userArgumentCaptor.capture());
    assertNull(userArgumentCaptor.getValue().getPhoto());
  }

  @Test
  public void shouldUploadUserWithoutPictureInLocal() throws Exception {
    User user = userWithPhoto();
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(localUserRepository.getUserById(USER_ID)).thenReturn(user);

    removeUserPhotoInteractor.removeUserPhoto(callback, errorCallback);

    verify(localUserRepository).putUser(userArgumentCaptor.capture());
    assertNull(userArgumentCaptor.getValue().getPhoto());
  }

  @Test
  public void shouldNotifyCompletedWhenUserUpdated() throws Exception {
    User user = userWithPhoto();
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(localUserRepository.getUserById(USER_ID)).thenReturn(user);

    removeUserPhotoInteractor.removeUserPhoto(callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test
  public void shouldNotifyErrorIfRemoteRepositoryThrowsException() throws Exception {
    User user = userWithPhoto();
    when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
    when(localUserRepository.getUserById(USER_ID)).thenReturn(user);
    doThrow(new ServerCommunicationException(new Throwable())).when(remoteUserRepository)
        .putUser(any(User.class));

    removeUserPhotoInteractor.removeUserPhoto(callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  private User userWithPhoto() {
    User user = new User();
    user.setPhoto(PHOTO_URL);
    return user;
  }
}
