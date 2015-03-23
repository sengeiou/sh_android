package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.bus.EventChanged;
import com.shootr.android.domain.interactor.event.ExitEventInteractor;
import com.shootr.android.domain.interactor.event.SelectEventInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.ui.views.EventSelectionView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class EventSelectionPresenterTest {

    private static final String EVENT_TAG = "tag";
    private static final Long EVENT_ID = 1L;

    @Mock EventSelectionView eventSelectionView;
    @Mock VisibleEventInfoInteractor visibleEventInfoInteractor;
    @Mock SelectEventInteractor selectEventInteractor;
    @Mock ExitEventInteractor exitEventInteractor;
    @Mock BusPublisher busPublisher;

    private EventSelectionPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EventSelectionPresenter(visibleEventInfoInteractor,
          selectEventInteractor,
          exitEventInteractor,
          busPublisher);
        presenter.setView(eventSelectionView);
    }

    @Test
    public void shouldShowCurrentEventTitleWhenPresenterInitialized() throws Exception {
        setupVisibleEventInteractorCallbacks(visibleEventInfo());

        presenter.loadCurrentEventTitle();

        verify(eventSelectionView).showCurrentEventTitle(anyString());
    }

    @Test
    public void shouldShowCurrentEventTagAsTitleWhenThereIsCurrentEvent() throws Exception {
        setupVisibleEventInteractorCallbacks(visibleEventInfo());

        presenter.loadCurrentEventTitle();

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventSelectionView).showCurrentEventTitle(titleCaptor.capture());
        assertThat(titleCaptor.getValue()).isEqualTo(EVENT_TAG);
    }

    @Test
    public void shouldShowHallTitleWhenNoCurrentEvent() throws Exception {
        setupVisibleEventInteractorCallbacks(noEventInfo());

        presenter.loadCurrentEventTitle();

        verify(eventSelectionView).showHallTitle();
    }

    @Test
    public void shouldPostEventToBusPublisherWhenEventSelected() throws Exception {
        setupSelectEventInteractorCallbacksVisibleEventWatch();

        presenter.onEventSelected(EVENT_ID);

        ArgumentCaptor<EventChanged.Event> captor = ArgumentCaptor.forClass(EventChanged.Event.class);
        verify(busPublisher).post(captor.capture());
        Long newEventId = captor.getValue().getNewEventId();
        assertThat(newEventId).isEqualTo(EVENT_ID);
    }

    @Test
    public void shouldShowCurrentEventTitleWhenEventSelected() throws Exception {
        setupSelectEventInteractorCallbacksVisibleEventWatch();
        setupVisibleEventInteractorCallbacks(visibleEventInfo());

        presenter.onEventSelected(EVENT_ID);

        verify(eventSelectionView).showCurrentEventTitle(anyString());
    }

    @Test
    public void shouldPostEventToBusPublisherWhenExitEvent() throws Exception {
        setupExitEventInteractorCallbacks();

        presenter.exitEventClick();

        ArgumentCaptor<EventChanged.Event> captor = ArgumentCaptor.forClass(EventChanged.Event.class);
        verify(busPublisher).post(captor.capture());
        Long newEventId = captor.getValue().getNewEventId();
        assertThat(newEventId).isNull();
    }

    @Test
    public void shouldshowHallTitleWhenExitEvent() throws Exception {
        setupExitEventInteractorCallbacks();
        setupVisibleEventInteractorCallbacks(noEventInfo());

        presenter.exitEventClick();

        verify(eventSelectionView).showHallTitle();
    }

    @Test
    public void shouldShowHallTitleWhenExitEventAlthoughEventInteractorHasntCallback() throws Exception {
        doNothing().when(visibleEventInfoInteractor)
          .obtainVisibleEventInfo(any(VisibleEventInfoInteractor.Callback.class));
        setupExitEventInteractorCallbacks();

        presenter.exitEventClick();

        verify(eventSelectionView).showHallTitle();
    }

    @Test
    public void shouldShowExitButtonWhenThereIsCurrentEvent() throws Exception {
        setupVisibleEventInteractorCallbacks(visibleEventInfo());

        presenter.loadCurrentEventTitle();

        verify(eventSelectionView).showExitButton();
    }

    @Test
    public void shouldHideExitButtonWhenNoCurrentEvent() throws Exception {
        setupVisibleEventInteractorCallbacks(noEventInfo());

        presenter.loadCurrentEventTitle();

        verify(eventSelectionView).hideExitButton();
    }

    private void setupSelectEventInteractorCallbacksVisibleEventWatch() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Long selectedEventId = (Long) invocation.getArguments()[0];
                ((SelectEventInteractor.Callback) invocation.getArguments()[1]).onLoaded(selectedEventId);
                return null;
            }
        }).when(selectEventInteractor).selectEvent(anyLong(), any(SelectEventInteractor.Callback.class));
    }

    private void setupExitEventInteractorCallbacks() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((ExitEventInteractor.Callback) invocation.getArguments()[0]).onLoaded();
                return null;
            }
        }).when(exitEventInteractor).exitEvent(any(ExitEventInteractor.Callback.class));
    }

    private EventInfo noEventInfo() {
        EventInfo eventInfo = new EventInfo();
        eventInfo.setEvent(null);
        return eventInfo;
    }

    private void setupVisibleEventInteractorCallbacks(final EventInfo eventInfo) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((VisibleEventInfoInteractor.Callback) invocation.getArguments()[0]).onLoaded(eventInfo);
                return null;
            }
        }).when(visibleEventInfoInteractor).obtainVisibleEventInfo(any(VisibleEventInfoInteractor.Callback.class));
    }

    private EventInfo visibleEventInfo() {
        EventInfo eventInfo = new EventInfo();
        eventInfo.setEvent(event());
        return eventInfo;
    }

    private Event event() {
        Event event = new Event();
        event.setTag(EVENT_TAG);
        return event;
    }
}