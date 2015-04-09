package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.ShotSender;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostNewShotInteractorTest {

    private static final Long CURRENT_USER_ID = 1L;
    private static final Long VISIBLE_EVENT_ID = 1L;
    private static final String COMMENT_EMPTY = "";
    private static final String COMMENT_STUB = "comment";
    private static final File IMAGE_NULL = null;
    private static final File IMAGE_STUB = new File(".");
    private static final String EVENT_TITLE_STUB = "title";
    private static final String EVENT_TAG_STUB = "tag";

    @Mock SessionRepository sessionRepository;
    @Mock EventRepository localEventRepository;
    @Mock ShotSender shotSender;

    private PostNewShotInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        InteractorHandler interactorHandler = new TestInteractorHandler();
        interactor =
          new PostNewShotInteractor(postExecutionThread, interactorHandler, sessionRepository, localEventRepository,
            shotSender);
    }

    @Test
    public void shouldSendShotWithCurrentUserInfo() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShot(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotUserInfo userInfo = publishedShot.getUserInfo();
        assertUserInfoIsFromUser(userInfo, currentUser());
    }

    @Test
    public void shouldSendShotWithVisibleEventInfoWhenThereIsVisibleEvent() throws Exception {
        setupCurrentUserSession();
        setupVisibleEvent();

        interactor.postNewShot(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotEventInfo eventInfo = publishedShot.getEventInfo();
        assertEventInfoIsFromEvent(eventInfo, visibleEvent());
    }

    @Test
    public void shouldSendShotWithoutEventInfoWhenNoEventVisible() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShot(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        Shot.ShotEventInfo eventInfo = publishedShot.getEventInfo();
        assertThat(eventInfo).isNull();
    }

    @Test
    public void shouldSendNullCommentWhenInputCommentIsEmpty() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShot(COMMENT_EMPTY, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        ArgumentCaptor<Shot> shotArgumentCaptor = ArgumentCaptor.forClass(Shot.class);
        verify(shotSender).sendShot(shotArgumentCaptor.capture(), any(File.class));
        Shot publishedShot = shotArgumentCaptor.getValue();
        assertThat(publishedShot.getComment()).isNull();
    }

    @Test
    public void shouldSendShotThroughDispatcher() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShot(COMMENT_STUB, IMAGE_NULL, new DummyCallback(), new DummyErrorCallback());

        verify(shotSender, times(1)).sendShot(any(Shot.class), any(File.class));
    }


    //region Assertion
    private void assertUserInfoIsFromUser(Shot.ShotUserInfo userInfo, User user) {
        assertThat(userInfo.getIdUser()).isEqualTo(user.getIdUser());
        assertThat(userInfo.getUsername()).isEqualTo(user.getUsername());
        assertThat(userInfo.getAvatar()).isEqualTo(user.getPhoto());
    }

    private void assertEventInfoIsFromEvent(Shot.ShotEventInfo eventInfo, Event event) {
        assertThat(eventInfo.getIdEvent()).isEqualTo(event.getId());
        assertThat(eventInfo.getEventTitle()).isEqualTo(event.getTitle());
        assertThat(eventInfo.getEventTag()).isEqualTo(event.getTag());
    }
    //endregion

    //region Setup
    private void setupCurrentUserSession() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUser());
        when(sessionRepository.getCurrentUserId()).thenReturn(currentUser().getIdUser());
    }

    private void setupVisibleEvent() {
        when(sessionRepository.getCurrentUser()).thenReturn(currentUserWatching());
        when(localEventRepository.getEventById(VISIBLE_EVENT_ID)).thenReturn(visibleEvent());
    }

    //endregion

    //region Stubs
    private Event visibleEvent() {
        Event event = new Event();
        event.setId(VISIBLE_EVENT_ID);
        event.setTitle(EVENT_TITLE_STUB);
        event.setTag(EVENT_TAG_STUB);
        return event;
    }

    private User currentUserWatching() {
        User user = currentUser();
        user.setVisibleEventId(VISIBLE_EVENT_ID);
        return user;
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        user.setUsername("currentUsername");
        user.setPhoto("http://avatar.jpg");
        return user;
    }
    //endregion

    private class DummyCallback implements PostNewShotInteractor.Callback {

        @Override public void onLoaded() {
        }
    }

    private class SpyErrorCallback implements Interactor.ErrorCallback {

        public ShootrException error;

        @Override public void onError(ShootrException error) {
            this.error = error;
        }
    }

    private class DummyErrorCallback implements Interactor.ErrorCallback {

        @Override public void onError(ShootrException error) {
        }
    }
}
