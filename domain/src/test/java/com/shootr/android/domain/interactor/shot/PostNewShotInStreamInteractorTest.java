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

    private static final String WATCHING_EVENT_ID = "1L";

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
    public void shouldSendShotWithWatchingEventInfoWhenThereIsWatchingEvent() throws Exception {
        setupCurrentUserSession();
        setupWatchingEvent();

        interactor.postNewShotInStream(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotStreamInfo eventInfo = publishedShot.getStreamInfo();
        assertEventInfoIsFromEvent(eventInfo, watchingEvent());
    }

    @Test
    public void shouldSendShotWithoutEventInfoWhenNoEventWatching() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShotInStream(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotStreamInfo eventInfo = publishedShot.getStreamInfo();
        assertThat(eventInfo).isNull();
    }

    private void setupWatchingEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatching());
        when(localStreamRepository.getStreamById(WATCHING_EVENT_ID)).thenReturn(watchingEvent());
    }

    private Stream watchingEvent() {
        Stream stream = new Stream();
        stream.setId(String.valueOf(WATCHING_EVENT_ID));
        stream.setTitle(EVENT_TITLE_STUB);
        stream.setTag(EVENT_TAG_STUB);
        return stream;
    }

    private User currentUserWatching() {
        User user = currentUser();
        user.setIdWatchingStream(String.valueOf(WATCHING_EVENT_ID));
        return user;
    }
}