package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.mobile.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UpdateStreamInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewStreamPresenterTest {

    public static final String TITLE = "title";
    private static final String STREAM_ID = "streamId";
    private static final String USER_ID = "userId";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final Integer MODE = 0;
    public static final String URL = "URL";
    @Mock CreateStreamInteractor createStreamInteractor;
    @Mock GetStreamInteractor getStreamInteractor;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock NewStreamView newStreamView;
    @Mock SessionRepository sessionRepository;
    @Mock ChangeStreamPhotoInteractor changeStreamPhotoInteractor;
    @Mock UpdateStreamInteractor updateStreamInteractor;

    private NewStreamPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        presenter = new NewStreamPresenter(createStreamInteractor, updateStreamInteractor,
            getStreamInteractor, changeStreamPhotoInteractor, selectStreamInteractor,
          streamModelMapper,
          errorMessageFactory, sessionRepository);
    }

    @Test public void shouldUpdateDoneButtonStatusWhenEditTitle() {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged(TITLE);
        verify(newStreamView).doneButtonEnabled(false);
    }

    @Test public void shouldCloseScreenWithResultWhenEditStreamWithoutTopic() throws Exception {
        setupGetStreamInteractorCallbackWithEmptyTopic();
        setupUpdateStreamInteractorCallbackWithEmptyTopic();
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        presenter.initialize(newStreamView, STREAM_ID);

        presenter.done(TITLE, DESCRIPTION, MODE, URL);

        verify(newStreamView).closeScreenWithResult(anyString());
    }

    @Test public void shouldCloseScreenWithResultWhenCreateStreamAndTopicIsNull() throws Exception {
        setupCreateStreamInteractorCallbackWithEmptyTopic();
        when(sessionRepository.getCurrentUserId()).thenReturn(USER_ID);
        presenter.initialize(newStreamView, null);
        presenter.done(TITLE, DESCRIPTION, MODE, URL);

        presenter.confirmNotify(TITLE, DESCRIPTION, MODE, true, URL);

        verify(newStreamView).goToShareStream(anyString());
    }

    private void setupGetStreamInteractorCallbackWithEmptyTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetStreamInteractor.Callback) invocation.getArguments()[1]).onLoaded(selectedStreamWithNullTopic());
                return null;
            }
        }).when(getStreamInteractor).loadStream(anyString(), any(GetStreamInteractor.Callback.class));
    }

    private void setupUpdateStreamInteractorCallbackWithEmptyTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((UpdateStreamInteractor.Callback) invocation.getArguments()[6])
                    .onLoaded(selectedStreamWithNullTopic());
                return null;
            }
        }).when(updateStreamInteractor)
            .updateStream(
                anyString(),
                anyString(),
                anyString(),
                anyInt(),
                anyString(),
                anyString(),
                any(UpdateStreamInteractor.Callback.class),
                any(Interactor.ErrorCallback.class));
    }


    private void setupCreateStreamInteractorCallbackWithEmptyTopic() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((CreateStreamInteractor.Callback) invocation.getArguments()[6])
                  .onLoaded(selectedStreamWithNullTopic());
                return null;
            }
        }).when(createStreamInteractor)
          .sendStream(
            anyString(),
            anyString(),
            anyInt(),
            anyString(),
            anyString(),
            anyBoolean(),
            any(CreateStreamInteractor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private Stream selectedStreamWithNullTopic() {
        Stream stream = new Stream();
        stream.setId(STREAM_ID);
        stream.setTitle(TITLE);
        stream.setAuthorId(USER_ID);
        stream.setDescription("");
        stream.setVideoUrl(URL);
        return stream;
    }
}
