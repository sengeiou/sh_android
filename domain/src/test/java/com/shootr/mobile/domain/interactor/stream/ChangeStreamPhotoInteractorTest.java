package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.PhotoService;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.utils.ImageResizer;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeStreamPhotoInteractorTest {

  private static final String URL = "url";
  private static final String ID_STREAM = "idStream";
  @Mock ImageResizer imageResizer;
  @Mock PhotoService photoService;
  @Mock StreamRepository localStreamRepository;
  @Mock StreamRepository remoteStreamRepository;
  @Mock ChangeStreamPhotoInteractor.Callback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private ChangeStreamPhotoInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new ChangeStreamPhotoInteractor(interactorHandler, postExecutionThread, imageResizer,
            photoService, localStreamRepository, remoteStreamRepository);
  }

  @Test public void shouldNotifyLoadedWhenChangeStreamPhoto() throws Exception {
    when(photoService.uploadStreamImageAndGetUrl(any(File.class), anyString())).thenReturn(URL);
    when(localStreamRepository.getStreamById(anyString(), anyArray())).thenReturn(stream());

    interactor.changeStreamPhoto(ID_STREAM, file(), callback, errorCallback);

    verify(callback).onLoaded(any(Stream.class));
  }

  @Test public void shouldNotifyErrorWhenImageResizerThrowsException() throws Exception {
    doThrow(IOException.class).when(imageResizer).getResizedImageFile(file());
    when(photoService.uploadStreamImageAndGetUrl(any(File.class), anyString())).thenReturn(URL);
    when(localStreamRepository.getStreamById(anyString(), anyArray())).thenReturn(stream());

    interactor.changeStreamPhoto(ID_STREAM, file(), callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  @Test public void shouldNotifyErrorWhenRemoteStreamRepositoryThrowsException() throws Exception {
    doThrow(ServerCommunicationException.class).when(photoService)
        .uploadStreamImageAndGetUrl(any(File.class), anyString());
    when(localStreamRepository.getStreamById(anyString(), anyArray())).thenReturn(stream());

    interactor.changeStreamPhoto(ID_STREAM, file(), callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  @Test public void shouldNotifyErrorWhenImageResizerThrowsOutOfMemoryError() throws Exception {
    doThrow(OutOfMemoryError.class).when(imageResizer).getResizedImageFile(file());
    when(localStreamRepository.getStreamById(anyString(), anyArray())).thenReturn(stream());

    interactor.changeStreamPhoto(ID_STREAM, file(), callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  private Stream stream() {
    return new Stream();
  }

  private File file() {
    return new File(URL);
  }

  private String[] anyArray() {
    return any(String[].class);
  }
}
