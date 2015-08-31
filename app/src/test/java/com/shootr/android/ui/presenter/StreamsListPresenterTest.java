package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.android.domain.interactor.stream.StreamsListInteractor;
import com.shootr.android.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.StreamsListView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StreamsListPresenterTest {

    private static final String SELECTED_STREAM_ID = "selected_stream";
    private static final String SELECTED_STREAM_TITLE = "title";
    private static final String STREAM_AUTHOR_ID = "author";
    public static final String ID_STREAM = "idStream";

    @Mock Bus bus;
    @Mock StreamsListInteractor streamsListInteractor;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock UnwatchStreamInteractor unwatchStreamInteractor;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock ShareStreamInteractor shareStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock StreamsListView streamsListView;

    private StreamsListPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        StreamResultModelMapper streamResultModelMapper =
          new StreamResultModelMapper(streamModelMapper);
        presenter = new StreamsListPresenter(streamsListInteractor,
          addToFavoritesInteractor,
          unwatchStreamInteractor,
          selectStreamInteractor, shareStreamInteractor,
          streamResultModelMapper,
          errorMessageFactory);
        presenter.setView(streamsListView);
    }

    @Test public void shouldLoadStreamListOnInitialized() throws Exception {
        presenter.initialize(streamsListView);

        verify(streamsListInteractor).loadStreams(anyStreamsCallback(), anyErrorCallback());
    }

    @Test public void shouldNavigateToStreamTimelineWhenStreamSelected() throws Exception {
        presenter.selectStream(selectedStreamModel());

        verify(streamsListView).navigateToStreamTimeline(SELECTED_STREAM_ID, SELECTED_STREAM_TITLE);
    }

    @Test public void shouldNavigateToStreamDetailWhenNewStreamCreated() throws Exception {
        presenter.streamCreated(SELECTED_STREAM_ID);

        verify(streamsListView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
    }

    @Test public void shouldNavigateToStreamDetailWhenNewStreamCreatedIfSelectStreamInteractorCallbacksStreamId() throws Exception {
        presenter.streamCreated(SELECTED_STREAM_ID);

        verify(streamsListView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
    }

    @Test public void shouldRenderStreamListWhenStreamListInteractorCallbacksResults() throws Exception {
        setupStreamListInteractorCallbacks(Arrays.asList(streamResult(), streamResult()));

        presenter.loadDefaultStreamList();

        verify(streamsListView).renderStream(anyListOf(StreamResultModel.class));
    }

    @Test public void shouldHideLoadingWhenStreamListInteractorCallbacksResults() throws Exception {
        setupStreamListInteractorCallbacks(Collections.singletonList(streamResult()));

        presenter.loadDefaultStreamList();

        verify(streamsListView).hideLoading();
    }

    @Test public void shouldNotShowLoadingWhenStreamListInteractorCallbacksResults() throws Exception {
        setupStreamListInteractorCallbacks(Collections.singletonList(streamResult()));

        presenter.loadDefaultStreamList();

        verify(streamsListView, never()).showLoading();
    }

    @Test public void shouldShowLoadingWhenStreamListInteractorCallbacksEmpty() throws Exception {
        setupStreamListInteractorCallbacks(new ArrayList<StreamSearchResult>());

        presenter.loadDefaultStreamList();

        verify(streamsListView).showLoading();
    }

    @Test public void shouldLoadStreamListOnceWhenInitializedAndResumed() throws Exception {
        presenter.initialize(streamsListView);
        presenter.resume();

        verify(streamsListInteractor, times(1)).loadStreams(anyStreamsCallback(), anyErrorCallback());
    }

    @Test public void shouldLoadStreamListTwiceWhenInitializedPausedAndResumed() throws Exception {
        presenter.initialize(streamsListView);
        presenter.pause();
        presenter.resume();

        verify(streamsListInteractor, times(2)).loadStreams(anyStreamsCallback(), anyErrorCallback());
    }

    @Test public void shouldShowSharedStreamWhenShareStreamsCompletedCallback() throws Exception {
        setupShareStreamCompletedCallback();

        presenter.shareStream(streamModel());

        verify(streamsListView).showStreamShared();
    }

    @Test public void shouldShowErrorStreamWhenShareStreamsErrorCallback() throws Exception {
        setupShareStreamErrorCallback();

        presenter.shareStream(streamModel());

        verify(streamsListView).showError(anyString());
    }

    private void setupShareStreamErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(new ShootrException() {});
                return null;
            }
        }).when(shareStreamInteractor).shareStream(anyString(),
          any(Interactor.CompletedCallback.class),
          anyErrorCallback());
    }

    private void setupShareStreamCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                completedCallback.onCompleted();
                return null;
            }
        }).when(shareStreamInteractor).shareStream(anyString(),
          any(Interactor.CompletedCallback.class),
          anyErrorCallback());
    }

    private StreamResultModel streamModel() {
        StreamResultModel streamResultModel = new StreamResultModel();
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(ID_STREAM);
        streamResultModel.setStreamModel(streamModel);
        return streamResultModel;
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private Interactor.Callback<StreamSearchResultList> anyStreamsCallback() {
        return any(Interactor.Callback.class);
    }

    private void setupStreamListInteractorCallbacks(final List<StreamSearchResult> result) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<StreamSearchResultList> callback =
                  (Interactor.Callback<StreamSearchResultList>) invocation.getArguments()[0];
                callback.onLoaded(new StreamSearchResultList(result));
                return null;
            }
        }).when(streamsListInteractor).loadStreams(anyStreamsCallback(), anyErrorCallback());
    }

    private StreamSearchResult streamResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(selectedStream());
        return streamSearchResult;
    }

    private StreamResultModel selectedStreamModel() {
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(SELECTED_STREAM_ID);
        streamModel.setTitle(SELECTED_STREAM_TITLE);
        streamModel.setTag(SELECTED_STREAM_TITLE);
        StreamResultModel streamResultModel = new StreamResultModel();
        streamResultModel.setStreamModel(streamModel);
        return streamResultModel;
    }

    private Stream selectedStream() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTitle(SELECTED_STREAM_TITLE);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        return stream;
    }
}