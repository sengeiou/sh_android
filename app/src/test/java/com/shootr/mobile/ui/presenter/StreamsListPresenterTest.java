package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamsListInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.StreamsListView;
import com.shootr.mobile.util.ErrorMessageFactory;
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
    @Mock ShareStreamInteractor shareStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock StreamsListView streamsListView;
    @Mock GetMutedStreamsInteractor getMutedStreamsInteractor;
    @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    @Mock GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;
    @Mock MuteInteractor muteInteractor;
    @Mock UnmuteInteractor unmuteInterator;

    private StreamsListPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        StreamResultModelMapper streamResultModelMapper =
            new StreamResultModelMapper(streamModelMapper);
        presenter = new StreamsListPresenter(streamsListInteractor, addToFavoritesInteractor,
            removeFromFavoritesInteractor, getFavoriteStreamsInteractor, unwatchStreamInteractor,
            shareStreamInteractor, getMutedStreamsInteractor, muteInteractor, unmuteInterator,
            streamResultModelMapper, errorMessageFactory, bus);
        presenter.setView(streamsListView);
    }

    @Test public void shouldLoadStreamListOnInitialized() throws Exception {
        presenter.initialize(streamsListView);

        verify(getFavoriteStreamsInteractor, times(1)).loadFavoriteStreamsFromLocalOnly(any(Interactor.Callback.class));
    }

    @Test public void shouldNavigateToStreamTimelineWhenStreamSelected() throws Exception {
        presenter.selectStream(selectedStreamModel());

        verify(streamsListView).navigateToStreamTimeline(SELECTED_STREAM_ID, SELECTED_STREAM_TITLE, STREAM_AUTHOR_ID);
    }

    @Test public void shouldNavigateToStreamDetailWhenNewStreamCreated() throws Exception {
        presenter.streamCreated(SELECTED_STREAM_ID);

        verify(streamsListView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
    }

    @Test public void shouldNavigateToStreamDetailWhenNewStreamCreatedIfSelectStreamInteractorCallbacksStreamId()
      throws Exception {
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

        verify(getFavoriteStreamsInteractor, times(1)).loadFavoriteStreamsFromLocalOnly(any(Interactor.Callback.class));
    }

    @Test public void shouldLoadStreamListTwiceWhenInitializedPausedAndResumed() throws Exception {
        presenter.initialize(streamsListView);
        presenter.pause();
        presenter.resume();

        verify(getFavoriteStreamsInteractor, times(2)).loadFavoriteStreamsFromLocalOnly(any(Interactor.Callback.class));
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

    @Test public void shouldShowContextMenuWithMuteStreamIfStreamNotMutedWhenLongPress() throws Exception {
        setupNoMutedStreamsCallback();

        presenter.onStreamLongClicked(streamModel());

        verify(streamsListView).showContextMenuWithMute(any(StreamResultModel.class));
    }

    @Test public void shouldShowContextMenuWithUnmuteStreamIfStreamMutedWhenLongPress() throws Exception {
        setupStreamIsMutedCallback();

        presenter.onStreamLongClicked(streamModel());

        verify(streamsListView).showContextMenuWithUnmute(any(StreamResultModel.class));
    }

    public void setupStreamIsMutedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<String>> callback =
                  (Interactor.Callback<List<String>>) invocation.getArguments()[0];
                callback.onLoaded(mutedStreams());
                return null;
            }
        }).when(getMutedStreamsInteractor).loadMutedStreamIds(any(Interactor.Callback.class));
    }

    public void setupNoMutedStreamsCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<String>> callback =
                  (Interactor.Callback<List<String>>) invocation.getArguments()[0];
                callback.onLoaded(Collections.<String>emptyList());
                return null;
            }
        }).when(getMutedStreamsInteractor).loadMutedStreamIds(any(Interactor.Callback.class));
    }

    @Test public void shouldCallbackMuteInteractorWhenMutePressed() throws Exception {
        presenter.onMuteClicked(streamModel());

        verify(muteInteractor).mute(anyString(), any(Interactor.CompletedCallback.class));
    }

    @Test public void shouldCallbackUnmuteInteractorWhenUnmutePressed() throws Exception {
        presenter.onUnmuteClicked(streamModel());

        verify(unmuteInterator).unmute(anyString(), any(Interactor.CompletedCallback.class));
    }

    private List<String> mutedStreams() {
        return Arrays.asList(ID_STREAM);
    }

    private void setupShareStreamErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(shareStreamInteractor)
          .shareStream(anyString(), any(Interactor.CompletedCallback.class), anyErrorCallback());
    }

    private void setupShareStreamCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                completedCallback.onCompleted();
                return null;
            }
        }).when(shareStreamInteractor)
          .shareStream(anyString(), any(Interactor.CompletedCallback.class), anyErrorCallback());
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
        streamModel.setAuthorId(STREAM_AUTHOR_ID);
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