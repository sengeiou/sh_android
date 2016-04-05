package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.RestoreStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.views.NewStreamView;
import com.shootr.mobile.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewStreamPresenterTest {

    public static final String SHORT_TITLE = "Short Title";
    public static final String OLD_TITLE = "old title";
    public static final String NEW_TITLE = "new title";
    public static final String TITLE = "title";
    public static final String TITLE_MORE_20_CHARS = "Title with more than tewnty and some characters";
    public static final String SHORT_TITLE_20_CHARS = "Title with more than";
    private static final String STREAM_ID = "streamId";
    private static final String USER_ID = "userId";
    @Mock CreateStreamInteractor createStreamInteractor;
    @Mock GetStreamInteractor getStreamInteractor;
    @Mock RemoveStreamInteractor removeStreamInteractor;
    @Mock RestoreStreamInteractor restoreStreamInteractor;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock NewStreamView newStreamView;
    @Mock SessionRepository sessionRepository;

    private NewStreamPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        presenter = new NewStreamPresenter(createStreamInteractor,
          getStreamInteractor,
          removeStreamInteractor,
          restoreStreamInteractor,
          selectStreamInteractor,
          streamModelMapper,
          errorMessageFactory);
    }

    @Test public void shouldChangeShortTitleWhenEditTitle() {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged(SHORT_TITLE);
        verify(newStreamView).showShortTitle(anyString());
    }

    @Test public void shouldHaveShortTitleWith15charactersMax() {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged(TITLE_MORE_20_CHARS);
        verify(newStreamView).showShortTitle(SHORT_TITLE_20_CHARS);
    }

    @Test public void shouldUpdateDoneButtonWhenEditShortTitle() {
        presenter.initialize(newStreamView, null);
        presenter.shortTitleTextChanged(SHORT_TITLE);
        verify(newStreamView, atLeastOnce()).doneButtonEnabled(anyBoolean());
    }

    @Test public void shouldUpdateDoneButtonStatusWhenEditTitle() {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged(TITLE);
        verify(newStreamView).showShortTitle(anyString());
    }

    @Test public void shouldShortTitleBeSameAsTitleWhenTitleEdited() throws Exception {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged(TITLE);

        verify(newStreamView).showShortTitle(TITLE);
    }

    @Test public void shouldShortTitleNotBeSameAsTitleWhenTitleEditedAfterShortTitleHasBeenEdited() throws Exception {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged(OLD_TITLE);
        presenter.shortTitleTextChanged(SHORT_TITLE);
        reset(newStreamView);

        presenter.titleTextChanged(NEW_TITLE);

        verify(newStreamView, never()).showShortTitle(anyString());
    }

    @Test public void shouldCloseScreenWithResultWhenEditStreamWithoutTopic() throws Exception {
        setupGetStreamInteractorCallbackWithEmptyTopic();
        setupCreateStreamInteractorCallbackWithEmptyTopic();
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        when(newStreamView.getStreamTitle()).thenReturn(TITLE);
        when(newStreamView.getStreamShortTitle()).thenReturn(SHORT_TITLE);
        when(newStreamView.getStreamDescription()).thenReturn("");
        presenter.initialize(newStreamView, STREAM_ID);

        presenter.done();

        verify(newStreamView).closeScreenWithResult(anyString());
    }

    @Test public void shouldCloseScreenWithResultWhenCreateStreamAndTopicIsNull() throws Exception {
        setupCreateStreamInteractorCallbackWithEmptyTopic();
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        when(newStreamView.getStreamTitle()).thenReturn(TITLE);
        when(newStreamView.getStreamShortTitle()).thenReturn(SHORT_TITLE);
        when(newStreamView.getStreamDescription()).thenReturn("");
        presenter.initialize(newStreamView, null);
        presenter.done();

        presenter.confirmNotify(true);

        verify(newStreamView).closeScreenWithResult(anyString());
    }

    private void setupGetStreamInteractorCallbackWithEmptyTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(selectedStreamWithNullTopic());
                return null;
            }
        }).when(getStreamInteractor).loadStream(anyString(), any(GetStreamInteractor.Callback.class));
    }

    private void setupCreateStreamInteractorCallbackWithEmptyTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CreateStreamInteractor.Callback) invocation.getArguments()[7]).onLoaded(selectedStreamWithNullTopic());
                return null;
            }
        }).when(createStreamInteractor)
          .sendStream(anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyBoolean(),
            anyBoolean(),
            any(CreateStreamInteractor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private Stream selectedStreamWithNullTopic() {
        Stream stream = new Stream();
        stream.setId(STREAM_ID);
        stream.setTitle(TITLE);
        stream.setShortTitle(SHORT_TITLE);
        stream.setAuthorId(USER_ID);
        stream.setDescription("");
        return stream;
    }
}
