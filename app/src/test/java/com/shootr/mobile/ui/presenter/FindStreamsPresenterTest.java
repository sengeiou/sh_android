package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetLocalStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamReactiveSearchInteractor;
import com.shootr.mobile.domain.interactor.stream.StreamSearchInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.FindStreamsView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FindStreamsPresenterTest {

    private static final String SELECTED_STREAM_ID = "selected_stream";
    private static final String SELECTED_STREAM_TITLE = "title";
    private static final String STREAM_AUTHOR_ID = "author";
    public static final String QUERY = "query";

    @Mock StreamSearchInteractor streamSearchInteractor;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock ShareStreamInteractor shareStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock FindStreamsView findStreamsView;
    @Mock GetLocalStreamsInteractor getLocalStreamsInteractor;
    @Mock StreamReactiveSearchInteractor streamReactiveSearchInteractor;
    @Mock UnwatchStreamInteractor unwatchStreamInteractor;

    private FindStreamsPresenter findStreamsPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        StreamResultModelMapper streamResultModelMapper = new StreamResultModelMapper(streamModelMapper);
        findStreamsPresenter = new FindStreamsPresenter(streamSearchInteractor,
          addToFavoritesInteractor,
          shareStreamInteractor, getLocalStreamsInteractor, streamReactiveSearchInteractor,
            unwatchStreamInteractor, streamResultModelMapper,
          errorMessageFactory);
        findStreamsPresenter.setView(findStreamsView);
    }

    @Test public void shouldHideStreamListWhileSearching() throws Exception {
        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).hideContent();
    }

    @Test public void shouldShowLoadingWhileSearching() throws Exception {
        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).showLoading();
    }

    @Test public void shouldHideLoadingWhenFinishSearching() throws Exception {
        setupSearchStreamInteractorCallbacks(Collections.singletonList(streamResult()));

        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).hideLoading();
    }

    @Test public void shouldHideLoadingWhenErrorWhileSearching() throws Exception {
        setupSearchStreamInteractorErrorCallbacks(Collections.singletonList(streamResult()));

        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).hideLoading();
    }

    @Test public void shouldShowStreamListWhenFinishSearching() throws Exception {
        setupSearchStreamInteractorCallbacks(Collections.singletonList(streamResult()));

        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).showContent();
    }

    @Test public void shouldNavigateToStreamTimeLineWhenStreamSelected() throws Exception {
        findStreamsPresenter.selectStream(streamResultModel());

        verify(findStreamsView).navigateToStreamTimeline(SELECTED_STREAM_ID, SELECTED_STREAM_TITLE,
          STREAM_AUTHOR_ID);
    }

    private void setupSearchStreamInteractorCallbacks(final List<StreamSearchResult> result) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                StreamSearchInteractor.Callback callback =
                  (StreamSearchInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(new StreamSearchResultList(result));
                return null;
            }
        }).when(streamSearchInteractor).searchStreams(anyString(), anyStreamSearchCallback(), anyErrorCallback());
    }

    private void setupSearchStreamInteractorErrorCallbacks(final List<StreamSearchResult> result) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                StreamSearchInteractor.ErrorCallback callback =
                  (StreamSearchInteractor.ErrorCallback) invocation.getArguments()[2];
                callback.onError(new ShootrException() {
                    @Override public Throwable fillInStackTrace() {
                        return super.fillInStackTrace();
                    }
                });
                return null;
            }
        }).when(streamSearchInteractor).searchStreams(anyString(), anyStreamSearchCallback(), anyErrorCallback());
    }

    private StreamSearchInteractor.Callback anyStreamSearchCallback() {
        return any(StreamSearchInteractor.Callback.class);
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private StreamSearchResult streamResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(selectedStream());
        return streamSearchResult;
    }

    private StreamResultModel streamResultModel() {
        StreamResultModel streamResultModel = new StreamResultModel();
        streamResultModel.setStreamModel(streamModel());
        return streamResultModel;
    }

    private StreamModel streamModel() {
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(SELECTED_STREAM_ID);
        streamModel.setTitle(SELECTED_STREAM_TITLE);
        streamModel.setAuthorId(STREAM_AUTHOR_ID);
        return streamModel;
    }

    private Stream selectedStream() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTitle(SELECTED_STREAM_TITLE);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        return stream;
    }
}
