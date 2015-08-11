package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostNewShotInStreamInteractorTest extends PostNewShotInteractorTestBase {

    private static final String WATCHING_STREAM_ID = "1L";

    @Mock StreamRepository localStreamRepository;

    private PostNewShotInStreamInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        InteractorHandler interactorHandler = new TestInteractorHandler();
        interactor =
          new PostNewShotInStreamInteractor(postExecutionThread, interactorHandler, sessionRepository,
            localStreamRepository, shotSender);
    }

    @Override protected PostNewShotInteractor getInteractorForCommonTests() {
        return interactor;
    }

    @Test
    public void shouldSendShotWithWatchingStreamInfoWhenThereIsWatchingStream() throws Exception {
        setupCurrentUserSession();
        setupWatchingStream();

        interactor.postNewShotInStream(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotStreamInfo streamInfo = publishedShot.getStreamInfo();
        assertStreamInfoIsFromStream(streamInfo, watchingStream());
    }

    @Test
    public void shouldSendShotWithoutStreamInfoWhenNoStreamWatching() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShotInStream(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotStreamInfo streamInfo = publishedShot.getStreamInfo();
        assertThat(streamInfo).isNull();
    }

    private void setupWatchingStream() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatching());
        when(localStreamRepository.getStreamById(WATCHING_STREAM_ID)).thenReturn(watchingStream());
    }

    private Stream watchingStream() {
        Stream stream = new Stream();
        stream.setId(String.valueOf(WATCHING_STREAM_ID));
        stream.setTitle(STREAM_TITLE_STUB);
        stream.setTag(STREAM_TAG_STUB);
        return stream;
    }

    private User currentUserWatching() {
        User user = currentUser();
        user.setIdWatchingStream(String.valueOf(WATCHING_STREAM_ID));
        return user;
    }
}