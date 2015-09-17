package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.android.domain.interactor.stream.StreamSearchInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.FindStreamsView;
import com.shootr.android.util.ErrorMessageFactory;
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

    private FindStreamsPresenter findStreamsPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        StreamResultModelMapper streamResultModelMapper =
          new StreamResultModelMapper(streamModelMapper);
        findStreamsPresenter = new FindStreamsPresenter(streamSearchInteractor,
          addToFavoritesInteractor, shareStreamInteractor, streamResultModelMapper, errorMessageFactory);
        findStreamsPresenter.setView(findStreamsView);
    }

    @Test
    public void shouldHideStreamListWhileSearching() throws Exception {
        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).hideContent();
    }

    @Test
    public void shouldShowLoadingWhileSearching() throws Exception {
        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenFinishSearching() throws Exception {
        setupSearchStreamInteractorCallbacks(Collections.singletonList(streamResult()));

        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).hideLoading();
    }

    @Test
    public void shouldHideLoadingWhenErrorWhileSearching() throws Exception {
        setupSearchStreamInteractorErrorCallbacks(Collections.singletonList(streamResult()));

        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).hideLoading();
    }

    @Test
    public void shouldShowStreamListWhenFinishSearching() throws Exception {
        setupSearchStreamInteractorCallbacks(Collections.singletonList(streamResult()));

        findStreamsPresenter.search(QUERY);

        verify(findStreamsView, times(1)).showContent();
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

    private Stream selectedStream() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTitle(SELECTED_STREAM_TITLE);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        return stream;
    }

}
