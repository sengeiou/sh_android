package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.StreamSearchInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.FindEventsView;
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

public class FindEventsPresenterTest {

    private static final String SELECTED_EVENT_ID = "selected_event";
    private static final String SELECTED_EVENT_TITLE = "title";
    private static final String EVENT_AUTHOR_ID = "author";
    public static final String QUERY = "query";

    @Mock StreamSearchInteractor streamSearchInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock FindEventsView findEventsView;

    private FindEventsPresenter findEventsPresenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        EventModelMapper eventModelMapper = new EventModelMapper(sessionRepository);
        EventResultModelMapper eventResultModelMapper =
          new EventResultModelMapper(eventModelMapper);
        findEventsPresenter = new FindEventsPresenter(streamSearchInteractor, eventResultModelMapper, errorMessageFactory);
        findEventsPresenter.setView(findEventsView);
    }

    @Test
    public void shouldHideEventListWhileSearching() throws Exception {
        findEventsPresenter.search(QUERY);

        verify(findEventsView, times(1)).hideContent();
    }

    @Test
    public void shouldShowLoadingWhileSearching() throws Exception {
        findEventsPresenter.search(QUERY);

        verify(findEventsView, times(1)).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenFinishSearching() throws Exception {
        setupSearchEventInteractorCallbacks(Collections.singletonList(eventResult()));

        findEventsPresenter.search(QUERY);

        verify(findEventsView, times(1)).hideLoading();
    }

    @Test
    public void shouldHideLoadingWhenErrorWhileSearching() throws Exception {
        setupSearchEventInteractorErrorCallbacks(Collections.singletonList(eventResult()));

        findEventsPresenter.search(QUERY);

        verify(findEventsView, times(1)).hideLoading();
    }

    @Test
    public void shouldShowEventListWhenFinishSearching() throws Exception {
        setupSearchEventInteractorCallbacks(Collections.singletonList(eventResult()));

        findEventsPresenter.search(QUERY);

        verify(findEventsView, times(1)).showContent();
    }

    private void setupSearchEventInteractorCallbacks(final List<StreamSearchResult> result) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                StreamSearchInteractor.Callback callback =
                  (StreamSearchInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(new StreamSearchResultList(result));
                return null;
            }
        }).when(streamSearchInteractor).searchStreams(anyString(), anyEventSearchCallback(), anyErrorCallback());
    }

    private void setupSearchEventInteractorErrorCallbacks(final List<StreamSearchResult> result) {
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
        }).when(streamSearchInteractor).searchStreams(anyString(), anyEventSearchCallback(), anyErrorCallback());
    }

    private StreamSearchInteractor.Callback anyEventSearchCallback() {
        return any(StreamSearchInteractor.Callback.class);
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private StreamSearchResult eventResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(selectedEvent());
        return streamSearchResult;
    }

    private Stream selectedEvent() {
        Stream stream = new Stream();
        stream.setId(SELECTED_EVENT_ID);
        stream.setTitle(SELECTED_EVENT_TITLE);
        stream.setAuthorId(EVENT_AUTHOR_ID);
        return stream;
    }

}
