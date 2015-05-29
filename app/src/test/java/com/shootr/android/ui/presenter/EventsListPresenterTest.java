package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.EventsListInteractor;
import com.shootr.android.domain.interactor.event.EventsSearchInteractor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.EventResultModelMapper;
import com.shootr.android.ui.views.EventsListView;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.utils.EventDateFormatterStub;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventsListPresenterTest {

    private static final String SELECTED_EVENT_ID = "selected_event";
    private static final String SELECTED_EVENT_TITLE = "title";
    private static final String EVENT_AUTHOR_ID = "author";

    @Mock Bus bus;
    @Mock EventsListInteractor eventsListInteractor;
    @Mock EventsSearchInteractor eventsSearchInteractor;
    @Mock SelectEventInteractor selectEventInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock EventsListView eventsListView;

    private EventsListPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        EventModelMapper eventModelMapper = new EventModelMapper(new EventDateFormatterStub(), sessionRepository);
        EventResultModelMapper eventResultModelMapper =
          new EventResultModelMapper(eventModelMapper);
        presenter = new EventsListPresenter(eventsListInteractor,
          eventsSearchInteractor, selectEventInteractor, eventResultModelMapper, eventModelMapper, errorMessageFactory);
        presenter.setView(eventsListView);
    }

    @Test public void shouldLoadEventListOnInitialized() throws Exception {
        presenter.initialize(eventsListView);

        verify(eventsListInteractor).loadEvents(anyEventsCallback(), anyErrorCallback());
    }

    @Test public void shouldSelectEventWithInteractorWhenEventSelected() throws Exception {
        presenter.selectEvent(selectedEventModel());

        verify(selectEventInteractor).selectEvent(eq(SELECTED_EVENT_ID), any(Interactor.Callback.class));
    }

    @Test public void shouldNavigateToEventTimelineWhenEventSelectedIfSelectEventInteractorCallbacksEventId() throws Exception {
        setupSelectEventInteractorCallbacksEvent();

        presenter.selectEvent(selectedEventModel());

        verify(eventsListView).navigateToEventTimeline(SELECTED_EVENT_ID, SELECTED_EVENT_TITLE);
    }

    @Test public void shouldSelectEventWithInteractorWhenNewEventCreated() throws Exception {
        presenter.eventCreated(SELECTED_EVENT_ID, SELECTED_EVENT_TITLE);

        verify(selectEventInteractor).selectEvent(eq(SELECTED_EVENT_ID), any(Interactor.Callback.class));
    }

    @Test public void shouldNavigateToEventTimelineWhenNewEventCreatedIfSelectEventInteractorCallbacksEventId() throws Exception {
        setupSelectEventInteractorCallbacksEvent();

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

    @Test public void shouldShowLoadingWhenEventListInteractorCallbacksEmpty() throws Exception {
        setupEventListInteractorCallbacks(new ArrayList<EventSearchResult>());

        presenter.loadDefaultEventList();

        verify(eventsListView).showLoading();
    }

    @Test public void shouldLoadEventListOnceWhenInitializedAndResumed() throws Exception {
        presenter.initialize(eventsListView);
        presenter.resume();

        verify(eventsListInteractor, times(1)).loadEvents(anyEventsCallback(), anyErrorCallback());
    }

    @Test public void shouldLoadEventListTwiceWhenInitializedPausedAndResumed() throws Exception {
        presenter.initialize(eventsListView);
        presenter.pause();
        presenter.resume();

        verify(eventsListInteractor, times(2)).loadEvents(anyEventsCallback(), anyErrorCallback());
    }

    //TODO search tests

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private Interactor.Callback<EventSearchResultList> anyEventsCallback() {
        return any(Interactor.Callback.class);
    }

    private void setupSelectEventInteractorCallbacksEvent() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<EventSearchResult> callback = (Interactor.Callback<EventSearchResult>) invocation.getArguments()[1];
                callback.onLoaded(eventResult());
                return null;
            }
        }).when(selectEventInteractor).selectEvent(anyString(), any(Interactor.Callback.class));
    }

    private void setupEventListInteractorCallbacks(final List<EventSearchResult> result) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<EventSearchResultList> callback =
                  (Interactor.Callback<EventSearchResultList>) invocation.getArguments()[0];
                callback.onLoaded(new EventSearchResultList(result));
                return null;
            }
        }).when(eventsListInteractor).loadEvents(anyEventsCallback(), anyErrorCallback());
    }

    private EventSearchResult eventResult() {
        EventSearchResult eventSearchResult = new EventSearchResult();
        eventSearchResult.setEvent(selectedEvent());
        return eventSearchResult;
    }

    private EventModel selectedEventModel() {
        EventModel eventModel = new EventModel();
        eventModel.setIdEvent(SELECTED_EVENT_ID);
        eventModel.setTitle(SELECTED_EVENT_TITLE);
        return eventModel;
    }

    private Event selectedEvent() {
        Event event = new Event();
        event.setId(SELECTED_EVENT_ID);
        event.setTitle(SELECTED_EVENT_TITLE);
        event.setStartDate(new Date());
        event.setAuthorId(EVENT_AUTHOR_ID);
        return event;
    }
}