package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.StreamSearchResultList;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.StreamsListInteractor;
import com.shootr.android.domain.interactor.event.UnwatchStreamInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.EventsListView;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventsListPresenterTest {

    private static final String SELECTED_EVENT_ID = "selected_event";
    private static final String SELECTED_EVENT_TITLE = "title";
    private static final String EVENT_AUTHOR_ID = "author";

    @Mock Bus bus;
    @Mock StreamsListInteractor streamsListInteractor;
    @Mock UnwatchStreamInteractor unwatchStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock EventsListView eventsListView;

    private EventsListPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        EventModelMapper eventModelMapper = new EventModelMapper(sessionRepository);
        EventResultModelMapper eventResultModelMapper =
          new EventResultModelMapper(eventModelMapper);
        presenter = new EventsListPresenter(streamsListInteractor, unwatchStreamInteractor,
          eventResultModelMapper,
          errorMessageFactory);
        presenter.setView(eventsListView);
    }

    @Test public void shouldLoadEventListOnInitialized() throws Exception {
        presenter.initialize(eventsListView);

        verify(streamsListInteractor).loadStreams(anyEventsCallback(), anyErrorCallback());
    }

    @Test public void shouldNavigateToEventTimelineWhenEventSelected() throws Exception {
        presenter.selectEvent(selectedEventModel());

        verify(eventsListView).navigateToEventTimeline(SELECTED_EVENT_ID, SELECTED_EVENT_TITLE);
    }

    @Test public void shouldNavigateToEventTimelineWhenNewEventCreated() throws Exception {
        presenter.eventCreated(SELECTED_EVENT_ID, SELECTED_EVENT_TITLE);

        verify(eventsListView).navigateToEventTimeline(SELECTED_EVENT_ID, SELECTED_EVENT_TITLE);
    }

    @Test public void shouldNavigateToEventTimelineWhenNewEventCreatedIfSelectEventInteractorCallbacksEventId() throws Exception {
        presenter.eventCreated(SELECTED_EVENT_ID, SELECTED_EVENT_TITLE);

        verify(eventsListView).navigateToEventTimeline(SELECTED_EVENT_ID, SELECTED_EVENT_TITLE);
    }

    @Test public void shouldRenderEventListWhenEventListInteractorCallbacksResults() throws Exception {
        setupEventListInteractorCallbacks(Arrays.asList(eventResult(), eventResult()));

        presenter.loadDefaultEventList();

        verify(eventsListView).renderEvents(anyListOf(EventResultModel.class));
    }

    @Test public void shouldHideLoadingWhenEventListInteractorCallbacksResults() throws Exception {
        setupEventListInteractorCallbacks(Collections.singletonList(eventResult()));

        presenter.loadDefaultEventList();

        verify(eventsListView).hideLoading();
    }

    @Test public void shouldNotShowLoadingWhenEventListInteractorCallbacksResults() throws Exception {
        setupEventListInteractorCallbacks(Collections.singletonList(eventResult()));

        presenter.loadDefaultEventList();

        verify(eventsListView, never()).showLoading();
    }

    @Test public void shouldShowLoadingWhenEventListInteractorCallbacksEmpty() throws Exception {
        setupEventListInteractorCallbacks(new ArrayList<StreamSearchResult>());

        presenter.loadDefaultEventList();

        verify(eventsListView).showLoading();
    }

    @Test public void shouldLoadEventListOnceWhenInitializedAndResumed() throws Exception {
        presenter.initialize(eventsListView);
        presenter.resume();

        verify(streamsListInteractor, times(1)).loadStreams(anyEventsCallback(), anyErrorCallback());
    }

    @Test public void shouldLoadEventListTwiceWhenInitializedPausedAndResumed() throws Exception {
        presenter.initialize(eventsListView);
        presenter.pause();
        presenter.resume();

        verify(streamsListInteractor, times(2)).loadStreams(anyEventsCallback(), anyErrorCallback());
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private Interactor.Callback<StreamSearchResultList> anyEventsCallback() {
        return any(Interactor.Callback.class);
    }

    private void setupEventListInteractorCallbacks(final List<StreamSearchResult> result) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<StreamSearchResultList> callback =
                  (Interactor.Callback<StreamSearchResultList>) invocation.getArguments()[0];
                callback.onLoaded(new StreamSearchResultList(result));
                return null;
            }
        }).when(streamsListInteractor).loadStreams(anyEventsCallback(), anyErrorCallback());
    }

    private StreamSearchResult eventResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        streamSearchResult.setStream(selectedEvent());
        return streamSearchResult;
    }

    private EventResultModel selectedEventModel() {
        EventModel eventModel = new EventModel();
        eventModel.setIdEvent(SELECTED_EVENT_ID);
        eventModel.setTitle(SELECTED_EVENT_TITLE);
        eventModel.setTag(SELECTED_EVENT_TITLE);
        EventResultModel eventResultModel = new EventResultModel();
        eventResultModel.setEventModel(eventModel);
        return eventResultModel;
    }

    private Stream selectedEvent() {
        Stream stream = new Stream();
        stream.setId(SELECTED_EVENT_ID);
        stream.setTitle(SELECTED_EVENT_TITLE);
        stream.setAuthorId(EVENT_AUTHOR_ID);
        return stream;
    }
}