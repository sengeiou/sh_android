package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostNewShotInStreamInteractorTest extends PostNewShotInteractorTestBase {

  private static final String WATCHING_STREAM_ID = "1L";
  String[] TYPES_STREAM = StreamMode.TYPES_STREAM;

  @Mock StreamRepository localStreamRepository;

  private PostNewShotInStreamInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();
    interactor =
        new PostNewShotInStreamInteractor(postExecutionThread, interactorHandler, sessionRepository,
            localStreamRepository, messageSender);
  }

  @Override protected PostNewMessageInteractor getInteractorForCommonTests() {
    return interactor;
  }

  @Test public void shouldSendShotWithWatchingStreamInfoWhenThereIsWatchingStream()
      throws Exception {
    setupCurrentUserSession();
    setupWatchingStream();

    interactor.postNewShotInStream(COMMENT_STUB, IMAGE_NULL, new DummyCallback(),
        new DummyErrorCallback());

    ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
    verify(messageSender).sendMessage(shotArgumentCaptor.capture(), any(File.class));
    Shot publishedShot = shotArgumentCaptor.getValue();
    Shot.ShotStreamInfo streamInfo = publishedShot.getStreamInfo();
    assertStreamInfoIsFromStream(streamInfo, watchingStream());
  }

  private void setupWatchingStream() {
    when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatching());
    when(localStreamRepository.getStreamById(WATCHING_STREAM_ID, TYPES_STREAM)).thenReturn(
        watchingStream());
  }

  private Stream watchingStream() {
    Stream stream = new Stream();
    stream.setId(String.valueOf(WATCHING_STREAM_ID));
    stream.setTitle(STREAM_TITLE_STUB);
    return stream;
  }

  private User currentUserWatching() {
    User user = currentUser();
    user.setIdWatchingStream(String.valueOf(WATCHING_STREAM_ID));
    return user;
  }
}