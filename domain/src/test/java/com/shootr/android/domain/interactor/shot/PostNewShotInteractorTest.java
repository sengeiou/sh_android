package com.shootr.android.domain.interactor.shot;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.exception.DomainValidationException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.WatchRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
    private static final String IMAGE_STUB = "http://image.jpg";
    private static final String EVENT_TITLE_STUB = "title";
    private static final String EVENT_TAG_STUB = "tag";

    @Mock SessionRepository sessionRepository;
    @Mock EventRepository localEventRepository;
    @Mock WatchRepository localWatchRepository;
    @Mock ShotRepository remoteShotRepository;

    private PostNewShotInteractor interactor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        InteractorHandler interactorHandler = new TestInteractorHandler();
        interactor =
          new PostNewShotInteractor(postExecutionThread, interactorHandler, sessionRepository, localEventRepository,
            localWatchRepository, remoteShotRepository);
    }

    @Test
    public void shouldSendShotWithCurrentUserInfo() throws Exception {
        setupCurrentUserSession();
        setupShotRepositoryReturnsSameShot();

        SpyCallback callback = new SpyCallback();
        interactor.postNewShot(COMMENT_STUB, IMAGE_STUB, callback, new DummyErrorCallback());

        Shot.ShotUserInfo userInfo = callback.publishedShot.getUserInfo();
        assertUserInfoIsFromUser(userInfo, currentUser());
    }

    @Test
    public void shouldSendShotWithVisibleEventInfoWhenThereIsVisibleEvent() throws Exception {
        setupCurrentUserSession();
        setupVisibleEvent();
        setupShotRepositoryReturnsSameShot();

        SpyCallback callback = new SpyCallback();
        interactor.postNewShot(COMMENT_STUB, IMAGE_STUB, callback, new DummyErrorCallback());

        Shot publishedShot = callback.publishedShot;
        Shot.ShotEventInfo eventInfo = publishedShot.getEventInfo();
        assertEventInfoIsFromEvent(eventInfo, visibleEvent());
    }

    @Test
    public void shouldSendShotWithoutEventInfoWhenNoEventVisible() throws Exception {
        setupCurrentUserSession();
        setupShotRepositoryReturnsSameShot();

        SpyCallback callback = new SpyCallback();
        interactor.postNewShot(COMMENT_STUB, IMAGE_STUB, callback, new DummyErrorCallback());

        Shot publishedShot = callback.publishedShot;
        Shot.ShotEventInfo eventInfo = publishedShot.getEventInfo();
        assertThat(eventInfo).isNull();
    }

    @Test
    public void shouldSendNullCommentWhenInputCommentIsEmpty() throws Exception {
        setupCurrentUserSession();
        setupShotRepositoryReturnsSameShot();

        SpyCallback callback = new SpyCallback();
        interactor.postNewShot(COMMENT_EMPTY, IMAGE_STUB, callback, new DummyErrorCallback());

        Shot publishedShot = callback.publishedShot;
        assertThat(publishedShot.getComment()).isNull();
    }

    @Test
    public void shouldSendShotThroughRemoteRepository() throws Exception {
        setupCurrentUserSession();

        interactor.postNewShot(COMMENT_STUB, IMAGE_STUB, new DummyCallback(), new DummyErrorCallback());

        verify(remoteShotRepository, times(1)).putShot(any(Shot.class));
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
        when(localWatchRepository.getCurrentVisibleWatch()).thenReturn(visibleWatch());
        when(localEventRepository.getEventById(VISIBLE_EVENT_ID)).thenReturn(visibleEvent());
    }

    private void setupShotRepositoryReturnsSameShot() {
        when(remoteShotRepository.putShot(any(Shot.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        });
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

    private Watch visibleWatch() {
        Watch watch = new Watch();
        watch.setIdEvent(VISIBLE_EVENT_ID);
        watch.setUser(currentUser());
        return watch;
    }

    private User currentUser() {
        User user = new User();
        user.setIdUser(CURRENT_USER_ID);
        user.setUsername("currentUsername");
        user.setPhoto("http://avatar.jpg");
        return user;
    }
    //endregion

    private class SpyCallback implements PostNewShotInteractor.Callback {

        public Shot publishedShot;

        @Override public void onLoaded(Shot published) {
            this.publishedShot = published;
        }
    }

    private class DummyCallback implements PostNewShotInteractor.Callback {

        @Override public void onLoaded(Shot published) {
        }
    }

    private class SpyErrorCallback implements Interactor.InteractorErrorCallback {

        public ShootrException error;

        @Override public void onError(ShootrException error) {
            this.error = error;
        }
    }

    private class DummyErrorCallback implements Interactor.InteractorErrorCallback {

        @Override public void onError(ShootrException error) {
        }
    }
}
