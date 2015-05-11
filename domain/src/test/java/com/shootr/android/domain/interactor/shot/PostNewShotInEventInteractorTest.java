package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
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

public class PostNewShotInEventInteractorTest extends PostNewShotInteractorTestBase {

    private static final String VISIBLE_EVENT_ID = "1L";

    @Mock EventRepository localEventRepository;

    private PostNewShotInEventInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        InteractorHandler interactorHandler = new TestInteractorHandler();
        interactor =
          new PostNewShotInEventInteractor(postExecutionThread, interactorHandler, sessionRepository, localEventRepository, shotSender);
    }

    @Override protected PostNewShotInteractor getInteractorForCommonTests() {
        return interactor;
    }

    @Test
    public void shouldSendShotWithVisibleEventInfoWhenThereIsVisibleEvent() throws Exception {
        setupCurrentUserSession();
        setupVisibleEvent();

        interactor.postNewShotInEvent(COMMENT_STUB,
          IMAGE_NULL,
          new DummyCallback(),
          new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotEventInfo eventInfo = publishedShot.getEventInfo();
        assertEventInfoIsFromEvent(eventInfo, visibleEvent());
    }

    @Test
    public void shouldSendShotWithoutEventInfoWhenNoEventVisible() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShotInEvent(COMMENT_STUB,
          IMAGE_NULL,
          new DummyCallback(),
          new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotEventInfo eventInfo = publishedShot.getEventInfo();
        assertThat(eventInfo).isNull();
    }

    private void setupVisibleEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatching());
        when(localEventRepository.getEventById(VISIBLE_EVENT_ID)).thenReturn(visibleEvent());
    }

    private Event visibleEvent() {
        Event event = new Event();
        event.setId(String.valueOf(VISIBLE_EVENT_ID));
        event.setTitle(EVENT_TITLE_STUB);
        event.setTag(EVENT_TAG_STUB);
        return event;
    }

    private User currentUserWatching() {
        User user = currentUser();
        user.setIdWatchingEvent(String.valueOf(VISIBLE_EVENT_ID));
        return user;
    }
}