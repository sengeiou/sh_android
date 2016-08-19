package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ImageResizingException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.PhotoService;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UploadUserPhotoInteractorTest {
  public static final String ID_USER = "id_user";
  private UploadUserPhotoInteractor uploadUserPhotoInteractor;
  @Mock PhotoService photoService;
  @Mock SessionRepository sessionRepository;
  @Mock UserRepository localUserRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock ImageResizer imageResizer;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    uploadUserPhotoInteractor =
        new UploadUserPhotoInteractor(interactorHandler, postExecutionThread, photoService,
            sessionRepository, localUserRepository, remoteUserRepository, imageResizer);
  }

  @Test
  public void shouldResizeImage() throws Exception {
    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(imageResizer).getResizedCroppedImageFile(any(File.class));
  }

  @Test
  public void shouldGetCurrentUserIdFromSession() throws Exception {
    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(sessionRepository).getCurrentUserId();
  }

  @Test
  public void shouldGetUserFromLocalRepository() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(localUserRepository).getUserById(ID_USER);
  }

  @Test
  public void shouldPutUserWithPhotoInRemote() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getUserById(ID_USER)).thenReturn(user());

    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(remoteUserRepository).putUser(any(User.class));
  }

  @Test
  public void shouldPutUserWithPhotoInLocal() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getUserById(ID_USER)).thenReturn(user());

    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(localUserRepository).putUser(any(User.class));
  }

  @Test
  public void shouldNotifyCompleted() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    when(localUserRepository.getUserById(ID_USER)).thenReturn(user());

    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test
  public void shouldNotifyErrorWhenPutUserInRemoteThrowsException() throws Exception {
    doThrow(new ServerCommunicationException(new Throwable())).when(remoteUserRepository)
        .putUser(any(User.class));

    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test
  public void shouldNotifyErrorWhenResizingImageThrowsIOException() throws Exception {
    when(imageResizer.getResizedCroppedImageFile(any(File.class))).thenThrow(new IOException());

    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(errorCallback).onError(any(ImageResizingException.class));
  }

  @Test
  public void shouldNotifyErrorWhenResizingImageThrowsNullpointerException() throws Exception {
    when(imageResizer.getResizedCroppedImageFile(any(File.class))).thenThrow(
        new NullPointerException());

    uploadUserPhotoInteractor.uploadUserPhoto(photo(), callback, errorCallback);

    verify(errorCallback).onError(any(ImageResizingException.class));
  }

  private User user() {
    return new User();
  }

  private File photo() {
    return new File("");
  }
}
