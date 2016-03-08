package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.QueuedShot;
import com.shootr.mobile.domain.bus.ShotFailed;
import com.shootr.mobile.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamIsReadOnlyInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewShotBarPresenterTest {

    private static final ShotFailed.Event SHOT_FAILED_EVENT = null;
    private static final String STREAM_ID = "stream";
    private static final Boolean IS_IN_STREAMTIMELINE = true;
    public static final String AUTHOR_ID = "authorId";
    private static final Boolean IS__NOT_IN_STREAMTIMELINE = false;
    private static final String ANOTHER_ID = "anotherId";

    @Mock GetStreamIsReadOnlyInteractor getStreamIsRemovedInteractor;
    @Mock GetDraftsInteractor getDraftsInteractor;
    @Mock NewShotBarView newShotBarView;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;

    private NewShotBarPresenter presenter;
    private ShotFailed.Receiver shotFailedReceiver;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new NewShotBarPresenter(getStreamIsRemovedInteractor,
          getDraftsInteractor,
          errorMessageFactory,
          bus,
          sessionRepository);
        presenter.setView(newShotBarView);
        shotFailedReceiver = presenter;
    }

    @Test public void shouldCheckDraftsWhenInitialized() throws Exception {
        presenter.initialize(newShotBarView, STREAM_ID, IS_IN_STREAMTIMELINE);

        verify(getDraftsInteractor).loadDrafts(any(GetDraftsInteractor.Callback.class));
    }

    @Test public void shouldShowDraftsButtonWhenGetDraftsReturnsDrafts() throws Exception {
        setupDraftsInteractorCallbacks(draftsList());

        presenter.initialize(newShotBarView, STREAM_ID, IS_IN_STREAMTIMELINE);

        verify(newShotBarView).showDraftsButton();
    }

    @Test public void shouldHideDraftsButtonWhenGetDraftsReturnsEmpty() throws Exception {
        setupDraftsInteractorCallbacks(emptyDraftsList());

        presenter.initialize(newShotBarView, STREAM_ID, IS_IN_STREAMTIMELINE);

        verify(newShotBarView).hideDraftsButton();
    }

    @Test public void shouldShowDraftsButtonWhenShotFailedStreamReceived() throws Exception {
        setupDraftsInteractorCallbacks(draftsList());

        shotFailedReceiver.onShotFailed(SHOT_FAILED_EVENT);

        verify(newShotBarView).showDraftsButton();
    }

    @Test public void shouldHideDraftsButtonWhenResumedAndNoDraftsReturned() throws Exception {
        setupDraftsInteractorCallbacks(emptyDraftsList());

        presenter.pause();
        presenter.resume();

        verify(newShotBarView).hideDraftsButton();
    }

    @Test public void shouldShowDraftsButtonWhenResumedAndDraftsReturned() throws Exception {
        setupDraftsInteractorCallbacks(draftsList());

        presenter.pause();
        presenter.resume();

        verify(newShotBarView).showDraftsButton();
    }

    @Test public void shouldReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = ShotFailed.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod =
          shotFailedReceiver.getClass().getMethod(receiverMethodName, ShotFailed.Event.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    @Test public void shouldPickTopicWhenIsInStreamTimelineAndIsStreamHolder() throws Exception {
        presenter.initializeWithIdStreamAuthor(newShotBarView, STREAM_ID, AUTHOR_ID, IS_IN_STREAMTIMELINE);
        when(sessionRepository.getCurrentUserId()).thenReturn(AUTHOR_ID);

        presenter.newShotFromImage();

        verify(newShotBarView).pickTopic();
    }

    @Test public void shouldNotPickTopicWhenIsNotInStreamTimelineAndIsStreamHolder() throws Exception {
        presenter.initializeWithIdStreamAuthor(newShotBarView, STREAM_ID, AUTHOR_ID, IS__NOT_IN_STREAMTIMELINE);
        when(sessionRepository.getCurrentUserId()).thenReturn(AUTHOR_ID);

        presenter.newShotFromImage();

        verify(newShotBarView, never()).pickTopic();
    }

    @Test public void shouldNotPickTopicWhenIsInStreamTimelineAndIsNotStreamHolder() throws Exception {
        presenter.initializeWithIdStreamAuthor(newShotBarView, STREAM_ID, ANOTHER_ID, IS_IN_STREAMTIMELINE);
        when(sessionRepository.getCurrentUserId()).thenReturn(AUTHOR_ID);

        presenter.newShotFromImage();

        verify(newShotBarView, never()).pickTopic();
    }

    @Test public void shouldNotPickTopicWhenIsNotInStreamTimelineAndIsNotStreamHolder() throws Exception {
        presenter.initializeWithIdStreamAuthor(newShotBarView, STREAM_ID, ANOTHER_ID, IS__NOT_IN_STREAMTIMELINE);
        when(sessionRepository.getCurrentUserId()).thenReturn(AUTHOR_ID);

        presenter.newShotFromImage();

        verify(newShotBarView, never()).pickTopic();
    }

    private List<QueuedShot> draftsList() {
        return Arrays.asList(queuedShot());
    }

    private List<QueuedShot> emptyDraftsList() {
        return new ArrayList<>();
    }

    private QueuedShot queuedShot() {
        return new QueuedShot();
    }

    private void setupDraftsInteractorCallbacks(final List<QueuedShot> drafts) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetDraftsInteractor.Callback) invocation.getArguments()[0]).onLoaded(drafts);
                return null;
            }
        }).when(getDraftsInteractor).loadDrafts(any(GetDraftsInteractor.Callback.class));
    }
}