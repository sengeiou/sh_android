package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class EventSelectionPresenterTest {

    private static final String EVENT_TAG = "tag";

    @Mock EventSelectionView eventSelectionView;
    @Mock VisibleEventInfoInteractor visibleEventInfoInteractor;

    private EventSelectionPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EventSelectionPresenter(visibleEventInfoInteractor);
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

        presenter.initialize(eventSelectionView);

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